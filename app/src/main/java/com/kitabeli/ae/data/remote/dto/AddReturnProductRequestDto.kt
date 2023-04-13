package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddReturnProductRequestDto(
    @SerialName("itemId")
    val itemId: Long? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("details")
    val details: String? = null,
    @SerialName("refillQuantity")
    val refillQuantity: Int? = null,
    @SerialName("returnItemDTO")
    val returnItemDTO: List<ReturnProductRequestDto>? = null,
)