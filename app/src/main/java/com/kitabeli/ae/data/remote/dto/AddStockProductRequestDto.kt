package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddStockProductRequestDto(
    @SerialName("photoProof")
    val photoProof: String? = null,
    @SerialName("skuId")
    val skuId: Int? = null,
    @SerialName("skuName")
    val skuName: String? = null,
    @SerialName("stockCount")
    val stockCount: Int? = null,
    @SerialName("stockOpnameId")
    val stockOpnameId: Int? = null
)
