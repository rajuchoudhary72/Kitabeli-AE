package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SkuProducts(
    @SerialName("skuDTOList")
    val skuDTOList: List<SkuDTO>? = null
)

@Serializable
data class SkuDTO(
    @SerialName("name")
    val name: String? = null,
    @SerialName("skuId")
    val skuId: Int? = null
)