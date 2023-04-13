package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("aeId")
    val aeId: Int? = null,
    @SerialName("kiosCode")
    val kioskCode: String? = null,
    @SerialName("role")
    val role: String,
    @SerialName("jwtToken")
    val jwtToken: String,
    /*@SerialName("phone")
    val phone: String? = null,*/
    @SerialName("success")
    val success: Boolean
)