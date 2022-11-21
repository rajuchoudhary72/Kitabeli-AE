package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CompletePaymentRequestDto(
    @SerialName("otp")
    val otp: String? = null,
    @SerialName("stockOPNameReportId")
    val stockOPNameReportId: String? = null
)