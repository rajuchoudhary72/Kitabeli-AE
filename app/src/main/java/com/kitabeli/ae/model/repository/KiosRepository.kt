package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface KiosRepository {
    suspend fun initializeStock(kiosCode: String): Flow<KiosDto>

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

    fun generateReport(
        stockOpNameId: Int,
    ): Flow<Report?>

    suspend fun confirmReport(
        stockOPNameReportId: Int,
        totalAmountToBePaid: String,
        kiosOwnerSignURLFile: File,
        aeSignURLFile: File,
        reportFile: File,
    ): Flow<Report?>

    suspend fun cancelReport(
        stockOPNameReportId: Int,
    ): Flow<Boolean>

    fun getKiosStocks(
        stockOpNameId: Int,
    ): Flow<KiosDetail?>

    fun getBtnStatus(
        stockOpNameId: Int,
    ): Flow<BtnStatusDto?>

    fun markEligibleForQa(
        stockOpNameId: Int,
    ): Flow<MarkEligibleForQaResponseDto?>

    fun getSkuProducts(
        kiosCode: String,
    ): Flow<List<SkuDTO>?>

    suspend fun getKiosData(): Flow<KiosData>
    fun verifyOtp(stockOPNameReportId: Int, otp: String): Flow<Report?>
}