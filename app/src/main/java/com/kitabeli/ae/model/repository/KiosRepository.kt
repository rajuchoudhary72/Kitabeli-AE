package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.KiosDto
import kotlinx.coroutines.flow.Flow
import java.io.File

interface KiosRepository {
    fun initializeStock(kiosCode: String): Flow<KiosDto>

    fun addStockProduct(
        stockOpNameId: Int,
        skuId: Int,
        skuName: String,
        stockCount: Int,
        photoProof: String,
    ): Flow<KiosDto>

    fun uploadProductImage(
        stockOpNameId: Int,
        imageFile: File
    ): Flow<String>
}