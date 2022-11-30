package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class KiosData(
    @SerialName("items")
    val items: List<KiosItem>? = null,
    @SerialName("totalElements")
    val totalElements: Int? = null,
    @SerialName("totalPages")
    val totalPages: Int? = null
)

@Serializable
data class KiosItem(
    @SerialName("aeCode")
    val aeCode: Int? = null,
    @SerialName("aeId")
    val aeId: Int? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("expireTime")
    val expireTime: String? = null,
    @SerialName("kiosCode")
    val kiosCode: String? = null,
    @SerialName("qaAssignedId")
    val qaAssignedId: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("stockOpnameId")
    val stockOpnameId: Int,
    @SerialName("stockOpnameItemDTOS")
    val stockOpnameItemDTOS: List<StockOpNameItemDTOS>? = null,
    @SerialName("stockOpnameLocalDate")
    val stockOpnameLocalDate: String? = null
)
