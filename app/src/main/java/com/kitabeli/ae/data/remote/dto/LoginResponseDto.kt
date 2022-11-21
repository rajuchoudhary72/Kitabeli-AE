package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("aeId")
    val aeId: Int,
    @SerialName("jwtToken")
    val jwtToken: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("success")
    val success: Boolean
)