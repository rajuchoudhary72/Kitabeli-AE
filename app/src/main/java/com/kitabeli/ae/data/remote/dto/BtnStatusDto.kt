package com.kitabeli.ae.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BtnStatusDto(
    @SerialName("isAjukanValidasiEnabled")
    val isAjukanValidasiEnabled: Boolean,
    @SerialName("isTandaTanganDokumenEnabled")
    val isTandaTanganDokumenEnabled: Boolean,
    @SerialName("showTambahProduk")
    val showTambahProduk: Boolean,
)