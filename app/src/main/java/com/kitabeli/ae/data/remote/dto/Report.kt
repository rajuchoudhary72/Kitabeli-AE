package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Report(
    @SerialName("accountExecutiveDTO")
    val accountExecutiveDTO: AccountExecutiveDTO? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("kioskDTO")
    val kioskDTO: KioskDTO? = null,
    @SerialName("offPlatformSaleAmount")
    val offPlatformSaleAmount: Int? = null,
    @SerialName("onPlatformAmount")
    val onPlatformAmount: Int? = null,
    @SerialName("stockOPNameReportItemDTOs")
    val stockOPNameReportItemDTOs: List<StockOPNameReportItemDTO?>? = null,
    @SerialName("totalAmountToBePaid")
    val totalAmountToBePaid: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("onPlatformSalesAmount")
    val onPlatformSalesAmount: Int? = null,
    @SerialName("offPlatformSalesAmount")
    val offPlatformSalesAmount: Int? = null
)

@Serializable
data class AccountExecutiveDTO(
    @SerialName("aeCode")
    val aeCode: String? = null,
    @SerialName("aeId")
    val aeId: Int? = null,
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
    val locationId: Int? = null,
    @SerialName("warehouseId")
    val warehouseId: Int? = null
)

@Serializable
data class StockOPNameReportItemDTO(
    @SerialName("amountToBePaid")
    val amountToBePaid: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("itemId")
    val itemId: Int? = null,
    @SerialName("itemName")
    val itemName: String? = null,
    @SerialName("lastStockOpCount")
    val lastStockOpCount: Int? = null,
    @SerialName("offPlatformSaleAmount")
    val offPlatformSaleAmount: Int? = null,
    @SerialName("offPlatformSaleQuantity")
    val offPlatformSaleQuantity: Int? = null,
    @SerialName("onPlatformAmount")
    val onPlatformAmount: Int? = null,
    @SerialName("onPlatformSaleQuantity")
    val onPlatformSaleQuantity: Int? = null,
    @SerialName("quantitiesToBePaid")
    val quantitiesToBePaid: Int? = null,
    @SerialName("stnItemCount")
    val stnItemCount: Int? = null,
    @SerialName("stockOpCount")
    val stockOpCount: Int? = null
)