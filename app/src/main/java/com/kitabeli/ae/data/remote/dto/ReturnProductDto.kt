package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReturnProductDto(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("createdBy")
    val createdBy: String? = null,
    @SerialName("updatedBy")
    val updatedBy: String? = null,
    @SerialName("itemId")
    val itemId: Long? = null,
    @SerialName("itemName")
    val itemName: String? = null,
    @SerialName("requestQuantity")
    val requestQuantity: Int? = null,
    @SerialName("refillQuantity")
    val refillQuantity: Int? = null,
    @SerialName("approvedQuantity")
    val approvedQuantity: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("reasonLabel")
    val reasonLabel: String? = null,
    @SerialName("details")
    val details: String? = null,
)