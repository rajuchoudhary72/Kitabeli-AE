package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ConfirmReportRequestDto(
    @SerialName("reportURL")
    val reportURL: String? = null,
    @SerialName("stockOPNameReportId")
    val stockOPNameReportId: String? = null,
    @SerialName("totalAmountToBePaid")
    val totalAmountToBePaid: String? = null
)