package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PaymentDetailDto(
    @SerialName("token")
    val token: String? = null,
    @SerialName("redirectUrl")
    val redirectUrl: String? = null,
    @SerialName("bank_name")
    val bankName: String? = null,
    @SerialName("virtual_account_number")
    val virtualAccountNumber: String? = null,
    @SerialName("order_id")
    val orderId: String? = null,
    @SerialName("order_amount")
    val orderAmount: String? = null,
    @SerialName("payment_expire_time")
    val paymentExpireTime: Long? = null,
    @SerialName("biller_code")
    val billerCode: String? = null,
    @SerialName("is_kiosk_shutdown")
    val is_kiosk_shutdown: Boolean? = false,
    @SerialName("payment_amount_type")
    val payment_amount_type: String? = null,
    @SerialName("partial_amount_confirmed_by_ae")
    val partial_amount_confirmed_by_ae: Boolean? = false,
)