package com.kitabeli.ae.ui.cashCollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.CancelReasonDto
import com.kitabeli.ae.model.LoadState
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
class CancelCashCollectionViewModel @Inject constructor(
    private val kiosRepository: KiosRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _isKioskOwner = sessionManager.isKioskOwner()
    val isKioskOwner = _isKioskOwner

    private val _cancelReason: MutableLiveData<UiState> = MutableLiveData()
    val cancelReason: LiveData<UiState> = _cancelReason

    fun getCancelReasonList() = viewModelScope.launch {
        kiosRepository.getCancelReasons()
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                if (response is LoadState.Loaded) {
                    var reasonList = response.value
                    isKioskOwner.collectLatest { isKioskOwner ->
                        if (isKioskOwner) {
                            reasonList = getReasonListForKioskOwner(response.value)
                        }
                        _cancelReason.postValue(UiState.Success(cancelReason = reasonList))
                    }
                } else {
                    _cancelReason.postValue(UiState.Error("Failed"))
                }
            }
    }

    private fun getReasonListForKioskOwner(response: List<CancelReasonDto>?): List<CancelReasonDto> {
        val noMoneyOption = response?.find {
            it.name == NO_MONEY
        } ?: CancelReasonDto()
        noMoneyOption.label = "Saya belum ada uang"
        val disagreeOption = response?.find {
            it.name == DISAGREE
        } ?: CancelReasonDto()
        disagreeOption.label = "Nominal penarikan tidak sesuai"
        val otherOption = response?.find { it.name == OTHERS }!!
        return listOf(noMoneyOption, disagreeOption, otherOption)
    }

    companion object {
        const val NO_MONEY = "KIOSK_OWNER_HAS_NO_MONEY"
        const val DISAGREE = "KIOSK_OWNER_DISAGREE"
        const val OTHERS = "OTHERS"
    }
}