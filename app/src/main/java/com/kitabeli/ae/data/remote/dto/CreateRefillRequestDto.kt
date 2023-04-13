package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRefillRequestDto(
    @SerialName("kioskCode")
    val kioskCode: String,
    @SerialName("transferType")
    val transferType: String,
    @SerialName("transferStockStatus")
    val transferStockStatus: String,
    @SerialName("requester")
    val requester: String,
)