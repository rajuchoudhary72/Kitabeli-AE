package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("role")
    val role: String
)