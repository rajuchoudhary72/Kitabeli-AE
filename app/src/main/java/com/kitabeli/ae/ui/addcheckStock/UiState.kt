package com.kitabeli.ae.ui.addcheckStock

import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.model.AppError

sealed interface UiState {
    object Loading : UiState

    data class Error(
        val error: AppError?,
    ) : UiState

    data class Success(
        val shouldShowBankAlert: Boolean? = null,
        val report: Report?
    ) : UiState

    fun isLoading() = this == Loading
}