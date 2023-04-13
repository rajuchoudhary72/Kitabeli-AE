package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KioskResignOtpDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("otp")
    val otp: String? = null,
    @SerialName("read")
    val read: Boolean? = null,
)