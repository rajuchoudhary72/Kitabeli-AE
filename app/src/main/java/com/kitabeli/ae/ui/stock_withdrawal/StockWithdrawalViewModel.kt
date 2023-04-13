package com.kitabeli.ae.ui.stock_withdrawal

import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.StockWithdrawalDto
import com.kitabeli.ae.data.remote.dto.StockWithdrawalOTPDto
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StockWithdrawalViewModel @Inject constructor(
    private val kiosRepository: KiosRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel() {


    fun getStockWithdrawalItems(stockTransferId: String, func: (StockWithdrawalDto?) -> Unit) {
        viewModelScope.launch {
            kiosRepository.getStockWithdrawalItems(
                stockTransferId = stockTransferId
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke(state.value)
                    }
                }
        }
    }


    fun submitOTP(
        stockTransferId: String,
        otp: String,
        deliveryProof: File,
        func: (StockWithdrawalOTPDto?) -> Unit
    ) {
        viewModelScope.launch {
            kiosRepository.submitStockWithdrawalOTP(
                aeEmail = sessionManager.getUserEmail().first(),
                stockTransferId = stockTransferId,
                otp = otp,
                deliveryProof = deliveryProof
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke(state.value)
                    }
                }
        }
    }

}