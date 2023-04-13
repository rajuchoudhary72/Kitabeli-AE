package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResendKioskResignOtpRequestDto(
    @SerialName("kioskCode")
    val kioskCode: String,
    @SerialName("formId")
    val formId: Int,
    @SerialName("isRead")
    val isRead: String,
)