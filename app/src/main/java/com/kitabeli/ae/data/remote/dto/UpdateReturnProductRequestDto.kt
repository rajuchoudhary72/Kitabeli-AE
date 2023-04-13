package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateReturnProductRequestDto(
    @SerialName("kioskCode")
    val kioskCode: String,
    @SerialName("itemId")
    val itemId: Long,
    @SerialName("itemName")
    val itemName: String,
    @SerialName("stockCount")
    val stockCount: Int,
    @SerialName("kioskReturnItemsReason")
    val kioskReturnItemsReason: String,
    @SerialName("expireLocalDate")
    val expireLocalDate: String?,
)