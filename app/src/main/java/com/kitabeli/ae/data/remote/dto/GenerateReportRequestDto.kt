package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GenerateReportRequestDto(
    @SerialName("stockOpnameId")
    val stockOpNameId: Int
)