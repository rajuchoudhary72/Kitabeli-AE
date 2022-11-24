package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MarkEligibleForQaRequestDto(
    @SerialName("stockOpnameId")
    val stockOpNameId: Int
)