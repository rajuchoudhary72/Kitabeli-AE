package com.kitabeli.ae.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApiError(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("payload")
    val payload: ErrorData?,
)

@Serializable
data class ErrorData(
    @SerialName("header")
    val header: String?,
    @SerialName("message")
    val message: String,
)