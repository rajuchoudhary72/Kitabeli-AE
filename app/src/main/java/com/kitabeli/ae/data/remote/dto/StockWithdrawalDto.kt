package com.kitabeli.ae.data.remote.dto


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockWithdrawalDto(
    @SerialName("created")
    val created: String? = null,
    @SerialName("itemDetailDTOS")
    val itemDetailDTOS: List<ItemDetailDTOS>? = null,
    @SerialName("transferStockId")
    val transferStockId: String? = null,
    @SerialName("updated")
    val updated: String? = null
) {
    @Serializable
    data class ItemDetailDTOS(
        @SerialName("csUom")
        val csUom: @Contextual Any? = null,
        @SerialName("image")
        val image: String? = null,
        @SerialName("images")
        val images: @Contextual Any? = null,
        @SerialName("itemId")
        val itemId: Int? = null,
        @SerialName("itemName")
        val itemName: String? = null,
        @SerialName("notes")
        val notes: String? = null,
        @SerialName("quantity")
        val quantity: Int? = null,
        @SerialName("quantityBad")
        val quantityBad: Int? = null,
        @SerialName("quantityGood")
        val quantityGood: Int? = null,
        @SerialName("remarks")
        val remarks: String? = null,
        var isSelected: Boolean = false
    )
}
