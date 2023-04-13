package com.kitabeli.ae.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelReasonDto(
    @SerialName("label")
    var label: String? = null,
    @SerialName("name")
    val name: String? = null
)
