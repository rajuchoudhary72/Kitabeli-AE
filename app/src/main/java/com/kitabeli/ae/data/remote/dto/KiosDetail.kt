package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class KiosDetail(
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
    val stockOnNameId: Int? = null,
    @SerialName("stockOpnameItemDTOS")
    val stockOpNameItemDTOS: List<StockOpNameItemDTOS>? = null,
    @SerialName("stockOpnameLocalDate")
    val stockOpNameLocalDate: String? = null
)

@Serializable
data class StockOpNameItemDTOS(
    @SerialName("note")
    val note: String? = null,
    @SerialName("photoProof")
    val photoProof: String? = null,
    @SerialName("skuId")
    val skuId: Int? = null,
    @SerialName("skuName")
    val skuName: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("stockCount")
    val stockCount: Int? = null,
    @SerialName("stockOpnameId")
    val stockOpNameId: Int? = null,
    @SerialName("stockOpnameItemId")
    val stockOpNameItemId: Int? = null
)