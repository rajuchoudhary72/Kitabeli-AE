package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyReturnRequestOtpDto(
    @SerialName("stockTransferId")
    val stockTransferId: String?,
    @SerialName("otp")
    val otp: String?
)