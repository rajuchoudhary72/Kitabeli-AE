package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReturnProductRequestDto(
    @SerialName("itemId")
    val itemId: Long? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("details")
    val details: String? = null,
    @SerialName("requestQuantity")
    val requestQuantity: Int? = null,
)