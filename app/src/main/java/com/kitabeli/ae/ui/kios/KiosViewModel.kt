package com.kitabeli.ae.ui.kios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.BtnStatusDto
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
import kotlinx.coroutines.flow.combine
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

    private val _btnStatus = MutableStateFlow<BtnStatusDto?>(null)
    val btnStatus = _btnStatus.asLiveData()

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)

    val isStockItemRejected =
        combine(
            flow = _btnStatus,
            flow2 = _kiosDetail
        ) { btnStatusDto, kiosDetail ->
            (btnStatusDto?.isTandaTanganDokumenEnabled?.not()
                ?: false) && (kiosDetail?.stockOpNameItemDTOS?.any { it.status == "QA_REJECTED" }
                ?: false)
        }.asLiveData()

    init {
        fetchKissDetails()
    }

    fun fetchKissDetails(refreshState: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {

            combine(
                flow = kiosRepository.getKiosStocks(stockOpNameId),
                flow2 = kiosRepository.getBtnStatus(stockOpNameId)
            ) { kiosDetail, btnStatusDto ->
                Pair(kiosDetail, btnStatusDto)
            }
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
                            _kiosDetail.update {
                                // state.value.first?.copy(stockOpNameItemDTOS = state.value.first?.stockOpNameItemDTOS?.map { it.copy() })
                                state.value.first
                            }
                            _btnStatus.update { state.value.second }
                        }
                    }
                }
        }
    }


    fun retry() {
        fetchKissDetails()
    }


    fun getKiosDetails(): KiosDetail? = _kiosDetail.value
    fun markEligibleForQa() {
        viewModelScope.launch {
            kiosRepository
                .markEligibleForQa(stockOpNameId)
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    state.getValueOrNull()?.let {
                        fetchKissDetails()
                    }
                }
        }

    }
}
