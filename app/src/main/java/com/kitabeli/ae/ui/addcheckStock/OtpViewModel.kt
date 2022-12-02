package com.kitabeli.ae.ui.addcheckStock

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.addcheckStock.OtpDialog.Companion.ID
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository,
) : BaseViewModel() {

    private val stockOPNameReportId = savedStateHandle.get<Int>(ID)
        ?: throw RuntimeException("stockOPNameReportId required, pass stockOPNameReportId in fragment arguments.")


    fun verifyOtp(otp: String, func: () -> Unit) {
        viewModelScope.launch {
            kiosRepository.verifyOtp(
                stockOPNameReportId = stockOPNameReportId,
                otp = otp
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke()
                    }
                }
        }

    }
}