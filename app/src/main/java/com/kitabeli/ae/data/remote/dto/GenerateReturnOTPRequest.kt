package com.kitabeli.ae.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GenerateReturnOTPRequest(
    @SerialName("aeId")
    val aeId: String?,
    @SerialName("kioskCode")
    val kioskCode: String?
)