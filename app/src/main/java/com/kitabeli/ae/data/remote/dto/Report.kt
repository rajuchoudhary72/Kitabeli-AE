package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Report(
    @SerialName("accountExecutiveDTO")
    val accountExecutiveDTO: AccountExecutiveDTO? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("kioskDTO")
    val kioskDTO: KioskDTO? = null,
    @SerialName("offPlatformSaleAmount")
    val offPlatformSaleAmount: String? = null,
    @SerialName("onPlatformAmount")
    val onPlatformAmount: String? = null,
    @SerialName("stockOPNameReportItemDTOs")
    val stockOPNameReportItemDTOs: List<StockOPNameReportItemDTO?>? = null,
    @SerialName("totalAmountToBePaid")
    val totalAmountToBePaid: String? = null
)

@Serializable
data class AccountExecutiveDTO(
    @SerialName("aeCode")
    val aeCode: String? = null,
    @SerialName("aeId")
    val aeId: String? = null,
    @SerialName("aeSignURL")
    val aeSignURL: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("legalName")
    val legalName: String? = null,
    @SerialName("phone")
    val phone: String? = null
)

@Serializable
data class KioskDTO(
    @SerialName("kioskCode")
    val kioskCode: String? = null,
    @SerialName("kioskName")
    val kioskName: String? = null,
    @SerialName("locationId")
    val locationId: String? = null,
    @SerialName("warehouseId")
    val warehouseId: String? = null
)

@Serializable
data class StockOPNameReportItemDTO(
    @SerialName("amountToBePaid")
    val amountToBePaid: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("itemId")
    val itemId: Int? = null,
    @SerialName("itemName")
    val itemName: String? = null,
    @SerialName("lastStockOpCount")
    val lastStockOpCount: String? = null,
    @SerialName("offPlatformSaleAmount")
    val offPlatformSaleAmount: String? = null,
    @SerialName("offPlatformSaleQuantity")
    val offPlatformSaleQuantity: String? = null,
    @SerialName("onPlatformAmount")
    val onPlatformAmount: String? = null,
    @SerialName("onPlatformSaleQuantity")
    val onPlatformSaleQuantity: String? = null,
    @SerialName("quantitiesToBePaid")
    val quantitiesToBePaid: String? = null,
    @SerialName("stnItemCount")
    val stnItemCount: String? = null,
    @SerialName("stockOpCount")
    val stockOpCount: String? = null
)