package com.kitabeli.ae.ui.kios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.LoadingState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KiosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _stockOpNameId = savedStateHandle.get<Int>("stockOpNameId")
        ?: throw RuntimeException("stockOpNameId required, Please pass stockOpNameId in fragment arguments.")
    val stockOpNameId = _stockOpNameId

    private val _kiosDetail = MutableStateFlow<KiosDetail?>(null)
    val kiosDetail = _kiosDetail.asStateFlow()

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)


    init {
        fetchKissDetails()
    }

    fun fetchKissDetails(refreshState: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            kiosRepository
                .getKiosStocks(stockOpNameId)
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
                            _kiosDetail.update { state.value }
                        }
                    }
                }
        }
    }


    fun retry() {
        fetchKissDetails()
    }


    fun getKiosDetails(): KiosDetail? = _kiosDetail.value

}
