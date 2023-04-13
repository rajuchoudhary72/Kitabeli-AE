package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KioskDetailDto(
    @SerialName("addressId")
    val addressId: Int? = null,
    @SerialName("isActivated")
    val isActivated: Boolean? = null,
    @SerialName("isActive")
    val isActive: Boolean? = null,
    @SerialName("isKioskLive")
    val isKioskLive: Boolean? = null,
    @SerialName("kiosOwnerName")
    val kiosOwnerName: String? = null,
    @SerialName("kiosUserId")
    val kiosUserId: Int? = null,
    @SerialName("kioskCode")
    val kioskCode: String? = null,
    @SerialName("kioskName")
    val kioskName: String? = null,
    @SerialName("locationId")
    val locationId: Int? = null,
    @SerialName("mitraId")
    val mitraId: Int? = null,
    @SerialName("mitraReferralCode")
    val mitraReferralCode: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("sourceWarehouseId")
    val sourceWarehouseId: Int? = null,
    @SerialName("temporaryClosed")
    val temporaryClosed: Boolean? = null,
    @SerialName("userId")
    val userId: Int? = null,
    @SerialName("warehouseId")
    val warehouseId: Int? = null
)