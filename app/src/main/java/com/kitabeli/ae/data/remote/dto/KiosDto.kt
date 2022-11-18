package com.kitabeli.ae.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class KiosDto(
    @SerialName("aeCode")
    val aeCode: String? = null,
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
    val stockOpnameId: Int? = null,
    @SerialName("stockOpnameItemDTOS")
    val stockOpnameItemDTOS: List<Product>? = null,
    @SerialName("stockOpnameLocalDate")
    val stockOpnameLocalDate: String? = null
) : Parcelable


@Parcelize
@Serializable
data class Product(
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
    val stockOpnameId: Int? = null,
    @SerialName("stockOpnameItemId")
    val stockOpnameItemId: Int? = null
) : Parcelable