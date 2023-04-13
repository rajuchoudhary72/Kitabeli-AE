package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitKioskResignFormRequestDto(
    @SerialName("formId")
    val formId: Int,
    @SerialName("aeId")
    val aeId: Int,
    @SerialName("kioskCode")
    val kioskCode: String,
    @SerialName("responses")
    val responses: Map<String, List<ResignOption>>,
)