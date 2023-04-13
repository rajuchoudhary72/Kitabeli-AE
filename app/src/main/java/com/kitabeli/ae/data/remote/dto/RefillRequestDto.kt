package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefillRequestDto(
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
    @SerialName("sourceWarehouseId")
    val sourceWarehouseId: Long? = null,
    @SerialName("destinationWarehouseId")
    val destinationWarehouseId: Long? = null,
    @SerialName("stockTransferId")
    val stockTransferId: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("refillRequestItems")
    val refillRequestItems: List<ReturnProductDto> = emptyList(),
    @SerialName("isFirstStockTransfer")
    val isFirstStockTransfer: Boolean? = null,
    @SerialName("transferType")
    val transferType: String? = null,
)