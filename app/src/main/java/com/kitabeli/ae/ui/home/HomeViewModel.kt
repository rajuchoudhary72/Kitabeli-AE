package com.kitabeli.ae.ui.home

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val kiosRepository: KiosRepository
) : BaseViewModel() {


    private val _kiosData = liveData {
        kiosRepository
            .getKiosData()
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { emit(it) }
    }
    val kiosData = _kiosData

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

    }
}