package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyReturnRequestOtpDto(
    @SerialName("kioskCode")
    val kioskCode: String?,
    @SerialName("otp")
    val otp: String?,
    @SerialName("aeId")
    val aeId: String?,
    @SerialName("aeEmail")
    val aeEmail: String?
)
