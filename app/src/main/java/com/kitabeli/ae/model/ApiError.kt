package com.kitabeli.ae.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApiError(
    @SerialName("errors")
    val errors: List<Error?>?,
    @SerialName("status")
    val status: Boolean?,
    @SerialName("statusCode")
    val statusCode: Int?,
) {
    fun errorMessage() = errors?.joinToString { it?.message ?: "" }
}

@Serializable
data class Error(
    @SerialName("code")
    val code: Int?,
    @SerialName("message")
    val message: String?,
)