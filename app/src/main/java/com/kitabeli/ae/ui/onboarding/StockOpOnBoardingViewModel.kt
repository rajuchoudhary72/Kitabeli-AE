package com.kitabeli.ae.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
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
class StockOpOnBoardingViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _kioskCode = sessionManager.getKioskCode()
    val kioskCode = _kioskCode

    fun initializeStock(kioskCode: String, onSuccess: (Int?) -> Unit) {
        viewModelScope.launch {
            kiosRepository
                .initializeStock(kioskCode)
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let {
                        onSuccess.invoke(it.stockOpnameId)
                    }
                }
        }
    }
}