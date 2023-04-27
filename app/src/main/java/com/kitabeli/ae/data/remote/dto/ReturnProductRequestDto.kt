package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReturnProductRequestDto(
    @SerialName("itemId")
    val itemId: Long? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("details")
    val details: String? = null,
    @SerialName("requestQuantity")
    val requestQuantity: Int? = null,
)

@Serializable
data class AddReplenishmentProductRequest(
    @SerialName("aeId")
    val aeId: String?,
    @SerialName("kioskCode")
    val kioskCode: String?,
    @SerialName("returnItemDTO")
    val returnItemDTO: ReturnItemDTO?
)

@Serializable
data class ReturnItemDTO(
    @SerialName("details")
    val details: String?,
    @SerialName("itemId")
    val itemId: Int?,
    @SerialName("itemName")
    val itemName: String?,
    @SerialName("reason")
    val reason: String?,
    @SerialName("requestQuantity")
    val requestQuantity: String?
)