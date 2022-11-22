package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SkuDTO(
    @SerialName("name")
    val name: String? = null,
    @SerialName("skuId")
    val skuId: Int? = null
)