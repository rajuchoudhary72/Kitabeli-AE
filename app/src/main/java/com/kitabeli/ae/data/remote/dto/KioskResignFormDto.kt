package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KioskResignFormDto(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("questionDTOS")
    val questionDTOS: List<ResignQuestion> = emptyList()
)

@Serializable
data class ResignQuestion(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("options")
    val options: List<ResignOption> = emptyList(),
    @SerialName("question")
    val question: String? = null,
    @SerialName("questionCode")
    val questionCode: String? = null,
    @SerialName("type")
    val type: String? = null
)

@Serializable
data class ResignOption(
    @SerialName("label")
    val label: String? = null,
    @SerialName("showTextBox")
    val showTextBox: Boolean? = null,
    @SerialName("value")
    val value: String? = null
)