package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InitializeStockRequestDto(
    @SerialName("kiosCode")
    val kiosCode: String,
    @SerialName("aeId")
    val aeId: Int,
    @SerialName("role")
    val role: String
)