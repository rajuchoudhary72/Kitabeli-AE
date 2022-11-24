package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.BtnStatusDto
import com.kitabeli.ae.data.remote.dto.KiosData
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.data.remote.dto.SkuDTO
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
        totalAmountToBePaid: Int,
        kiosOwnerSignURLFile: File,
        aeSignURLFile: File,
        reportFile: File,
    ): Flow<Report?>

    fun getKiosStocks(
        stockOpNameId: Int,
    ): Flow<KiosDetail?>

    fun getBtnStatus(
        stockOpNameId: Int,
    ): Flow<BtnStatusDto?>

    fun getSkuProducts(
        kiosCode: String,
    ): Flow<List<SkuDTO>?>

    suspend fun getKiosData(): Flow<KiosData>
    fun verifyOtp(stockOPNameReportId: Int, otp: String): Flow<Report?>
}