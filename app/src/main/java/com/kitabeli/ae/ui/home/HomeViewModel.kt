package com.kitabeli.ae.ui.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.KiosData
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.LoadingState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val kiosRepository: KiosRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {
    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)

    private val _kiosData = MutableStateFlow<KiosData?>(null)
    val kiosData = _kiosData.asLiveData()

    val isEmpty = combine(
        flow = loadingState,
        flow2 = _kiosData
    ) { a, b ->
        Pair(a, b)
    }.map {
        it.first.isSucceeded() && it.second?.items.isNullOrEmpty()
    }.asLiveData()

    init {
        fetchKiosData()
    }

    fun fetchKiosData(refreshState: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            kiosRepository
                .getKiosData()
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    refreshState?.invoke(state.isLoading)
                    when (state) {
                        LoadState.Loading -> {
                            if (refreshState == null)
                                loadingState.update { LoadingState.Loading }
                        }

                        is LoadState.Error -> {
                            if (refreshState == null) {
                                loadingState.update { LoadingState.Error(state.e) }
                            } else {
                                onError(state.getAppErrorIfExists()!!)
                            }
                        }

                        is LoadState.Loaded -> {
                            loadingState.update { LoadingState.Loaded }
                            _kiosData.update { state.value }
                        }
                    }
                }
        }
    }

    fun initializeStock(kiosCode: String, func: (KiosDto) -> Unit) {
        viewModelScope.launch {
            kiosRepository
                .initializeStock(kiosCode)
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let {
                        func(it)
                    }
                }
        }
    }

    fun retry() {
        fetchKiosData()
    }

    fun logout(func: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            func()
        }
    }
}