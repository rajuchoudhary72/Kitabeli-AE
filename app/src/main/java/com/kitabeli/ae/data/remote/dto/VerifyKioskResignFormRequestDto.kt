package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyKioskResignFormRequestDto(
    @SerialName("kioskCode")
    val kioskCode: String,
    @SerialName("otp")
    val otp: String,
    @SerialName("formId")
    val formId: Int,
)