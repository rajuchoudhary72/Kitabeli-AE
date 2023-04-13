package com.kitabeli.ae.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockWithdrawalOTPDto(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("status")
    val status: Status? = null
) {
    @Serializable
    data class Data(
        @SerialName("status")
        val status: Boolean? = null
    )

    @Serializable
    data class Status(
        @SerialName("httpCode")
        val httpCode: String? = null,
        @SerialName("message")
        val message: String? = null,
        @SerialName("success")
        val success: Boolean? = null
    )
}