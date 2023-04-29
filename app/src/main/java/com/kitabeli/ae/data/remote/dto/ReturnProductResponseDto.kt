package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ReturnProductResponseDto(
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("destinationWarehouseId")
    val destinationWarehouseId: Int?,
    @SerialName("itemCount")
    val itemCount: Int?,
    @SerialName("refillRequestItemDTOS")
    val refillRequestItemDTOS: List<RefillRequestItemDTOS>?,
    @SerialName("sourceWarehouseId")
    val sourceWarehouseId: Int?,
    @SerialName("status")
    val status: String?,
    @SerialName("statusImg")
    val statusImg: String?,
    @SerialName("statusStr")
    val statusStr: String?,
    @SerialName("stockTransferId")
    val stockTransferId: String?,
    @SerialName("totalQuantity")
    val totalQuantity: Int?,
    @SerialName("transferType")
    val transferType: String?,
    @SerialName("updatedAt")
    val updatedAt: String?
)

@Serializable
data class RefillRequestItemDTOS(
    @SerialName("approvedQuantity")
    val approvedQuantity: Int?,
    @SerialName("details")
    val details: String?,
    @SerialName("itemId")
    val itemId: Long?,
    @SerialName("itemName")
    val itemName: String?,
    @SerialName("reason")
    val reason: String?,
    @SerialName("reasonLabel")
    val reasonLabel: String?,
    @SerialName("refillQuantity")
    val refillQuantity: Int?,
    @SerialName("requestQuantity")
    val requestQuantity: Int?,
    @SerialName("status")
    val status: String?
)