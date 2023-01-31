package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CancelReportRequestDto(
    @SerialName("stockOPNameReportId")
    val stockOPNameReportId: Int? = null,
    @SerialName("cancelReason")
    val cancelReason: String? = null,
    @SerialName("note")
    val note: String? = null,
)