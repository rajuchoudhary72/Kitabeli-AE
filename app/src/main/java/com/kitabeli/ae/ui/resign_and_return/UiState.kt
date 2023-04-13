package com.kitabeli.ae.ui.resign_and_return

import com.kitabeli.ae.data.remote.dto.KioskResignFormDto
import com.kitabeli.ae.data.remote.dto.ReturnProductDto

sealed interface UiState {
    object Loading : UiState

    data class Error(
        val message: String?,
    ) : UiState

    data class Success(
        val resignForm: KioskResignFormDto? = null,
        val returnItemList: List<ReturnProductDto>? = null
    ) : UiState

    fun isLoading() = this == Loading
}