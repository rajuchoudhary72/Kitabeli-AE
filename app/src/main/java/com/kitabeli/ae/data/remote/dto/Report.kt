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
    val totalAmountToBePaid: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("onPlatformSalesAmount")
    val onPlatformSalesAmount: String? = null,
    @SerialName("offPlatformSalesAmount")
    val offPlatformSalesAmount: String? = null,
    @SerialName("incentiveAmount")
    val incentiveAmount: String? = null,
    @SerialName("partialAmountConfirmedByAE")
    val partialAmountConfirmedByAE: Boolean? = false,
    @SerialName("isKioskShutdown")
    val isKioskShutdown: Boolean? = false,
    @SerialName("totalPartialPaidAmount")
    val totalPartialPaidAmount: String? = null,
    @SerialName("totalPartialPendingAmount")
    val totalPartialPendingAmount: String? = null,
    @SerialName("stockTransferId")
    val stockTransferId: String? = null
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
    val warehouseId: Int? = null,
    @SerialName("mitraName")
    val mitraName: String? = null,
    @SerialName("mitraId")
    val mitraId: Int? = null
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
    val offPlatformSaleAmount: Long? = null,
    @SerialName("offPlatformSaleQuantity")
    val offPlatformSaleQuantity: Int? = null,
    @SerialName("onPlatformSaleAmount")
    val onPlatformSaleAmount: Long? = null,
    @SerialName("onPlatformSaleQuantity")
    val onPlatformSaleQuantity: Int? = null,
    @SerialName("quantitiesToBePaid")
    val quantitiesToBePaid: Int? = null,
    @SerialName("stnItemCount")
    val stnItemCount: Int? = null,
    @SerialName("stockOpCount")
    val stockOpCount: Int? = null,
    @SerialName("kiosOrderItemDetailsDTOList")
    val kioskOrderItemDetailsDTOList: List<KioskOrderItemDetailsDTO?>? = null,
)

@Serializable
data class KioskOrderItemDetailsDTO(
    @SerialName("amount")
    val amount: Long? = null,
    @SerialName("itemName")
    val itemName: String? = null,
    @SerialName("orderId")
    val orderId: Long? = null,
    @SerialName("orderPlacedAt")
    val orderPlacedAt: Long? = null,
    @SerialName("quantity")
    val quantity: Int? = null
)