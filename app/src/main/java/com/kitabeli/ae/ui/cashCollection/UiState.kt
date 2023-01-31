package com.kitabeli.ae.ui.cashCollection

import com.kitabeli.ae.data.remote.dto.CancelReasonDto

sealed interface UiState {
    object Loading : UiState

    data class Error(
        val message: String?,
    ) : UiState

    data class Success(
        val cancelReason: List<CancelReasonDto>?
    ) : UiState

    fun isLoading() = this == Loading
}