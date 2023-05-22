package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.BtnStatusDto
import com.kitabeli.ae.data.remote.dto.CancelReasonDto
import com.kitabeli.ae.data.remote.dto.KiosData
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.dto.KioskDetailDto
import com.kitabeli.ae.data.remote.dto.KioskResignFormDto
import com.kitabeli.ae.data.remote.dto.KioskResignOtpDto
import com.kitabeli.ae.data.remote.dto.MarkEligibleForQaResponseDto
import com.kitabeli.ae.data.remote.dto.PaymentDetailDto
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.data.remote.dto.ResignOption
import com.kitabeli.ae.data.remote.dto.SkuDTO
import androidx.core.net.toUri
import com.kitabeli.ae.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun updateStockProduct(
        stockOpNameId: Int,
        skuId: Int,
        skuName: String,
        stockCount: Int,
        photoProof: String,
        stockOpNameItemId: Int,
    ): Flow<KiosDto>

    fun uploadProductImage(
        stockOpNameId: Int,
        imageFile: File
    ): Flow<String>

    suspend fun generateReport(
        stockOpNameId: Int,
    ): Flow<Report?>

    suspend fun confirmReport(
        stockOPNameReportId: Int,
        totalAmountToBePaid: String,
        kiosOwnerSignURLFile: File,
        aeSignURLFile: File?,
        reportFile: File,
        KiosOwnerSignedBy: String,
        partialAmountConfirmedByAE: Boolean? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): Flow<PaymentDetailDto?>

    suspend fun cancelReport(
        stockOPNameReportId: Int,
        cancelReason: String,
        note: String
    ): Flow<Boolean>

    suspend fun getCancelReasons(): Flow<List<CancelReasonDto>?>

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

    fun getPaymentDetails(stockOpNameId: Int): Flow<PaymentDetailDto?>

    fun getKioskResignForm(): Flow<KioskResignFormDto?>

    fun resendKioskResignOtp(
        formId: Int,
        kioskCode: String,
    ): Flow<KioskResignOtpDto?>

    suspend fun submitKioskResignForm(
        formId: Int,
        kioskCode: String,
        responses: Map<String, List<ResignOption>>,
    ): Flow<KioskResignOtpDto?>

    fun verifyKioskResignOTP(
        kioskCode: String?,
        otp: String?,
        formId: Int?
    ): Flow<KioskResignOtpDto?>

    fun getKioskDetail(kioskCode: String?): Flow<KioskDetailDto?>

    fun getStockWithdrawalItems(stockTransferId: String): Flow<StockWithdrawalDto?>

    fun submitStockWithdrawalOTP(
        aeEmail: String,
        stockTransferId: String,
        deliveryProof: File,
        otp: String,
    ): Flow<StockWithdrawalOTPDto?>
}