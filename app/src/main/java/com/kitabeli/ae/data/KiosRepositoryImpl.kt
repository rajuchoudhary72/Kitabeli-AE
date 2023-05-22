package com.kitabeli.ae.data

import android.content.Context
import androidx.collection.arrayMapOf
import android.util.Log
import androidx.core.net.toUri
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.AddStockProductRequestDto
import com.kitabeli.ae.data.remote.dto.BtnStatusDto
import com.kitabeli.ae.data.remote.dto.CancelReasonDto
import com.kitabeli.ae.data.remote.dto.CancelReportRequestDto
import com.kitabeli.ae.data.remote.dto.CompletePaymentRequestDto
import com.kitabeli.ae.data.remote.dto.PaymentDetailDto
import com.kitabeli.ae.data.remote.dto.GenerateReportRequestDto
import com.kitabeli.ae.data.remote.dto.InitializeStockRequestDto
import com.kitabeli.ae.data.remote.dto.KiosData
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.dto.KioskDetailDto
import com.kitabeli.ae.data.remote.dto.KioskResignFormDto
import com.kitabeli.ae.data.remote.dto.KioskResignOtpDto
import com.kitabeli.ae.data.remote.dto.MarkEligibleForQaRequestDto
import com.kitabeli.ae.data.remote.dto.MarkEligibleForQaResponseDto
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.data.remote.dto.ResendKioskResignOtpRequestDto
import com.kitabeli.ae.data.remote.dto.ResignOption
import com.kitabeli.ae.data.remote.dto.SkuDTO
import com.kitabeli.ae.data.remote.dto.SubmitKioskResignFormRequestDto
import com.kitabeli.ae.data.remote.dto.VerifyKioskResignFormRequestDto
import com.kitabeli.ae.data.remote.dto.*
import com.kitabeli.ae.data.remote.service.KiosService
import com.kitabeli.ae.model.repository.KiosRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class KiosRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val kiosService: KiosService,
    private val sessionManager: SessionManager
) : KiosRepository {

    override suspend fun initializeStock(kiosCode: String): Flow<KiosDto> {
        return kiosService
            .initializeStock(
                InitializeStockRequestDto(
                    kiosCode = kiosCode,
                    aeId = sessionManager.getAeId().first(),
                    role = sessionManager.getUserRole().first()
                )
            )
            .map { it.payload!! }
    }

    override fun addStockProduct(
        stockOpNameId: Int,
        skuId: Int,
        skuName: String,
        stockCount: Int,
        photoProof: String
    ): Flow<KiosDto> {
        return kiosService
            .addStockProduct(
                AddStockProductRequestDto(
                    stockOpnameId = stockOpNameId,
                    photoProof = photoProof,
                    skuId = skuId,
                    skuName = skuName,
                    stockCount = stockCount
                )
            )
            .map { it.payload!! }
    }

    override fun updateStockProduct(
        stockOpNameId: Int,
        skuId: Int,
        skuName: String,
        stockCount: Int,
        photoProof: String,
        stockOpNameItemId: Int
    ): Flow<KiosDto> {
        return kiosService
            .updateStockProduct(
                stockOpnameItemId = stockOpNameItemId.toString(),
                AddStockProductRequestDto(
                    stockOpnameId = stockOpNameId,
                    photoProof = photoProof,
                    skuId = skuId,
                    skuName = skuName,
                    stockCount = stockCount
                )
            )
            .map { it.payload!! }
    }

    override fun uploadProductImage(stockOpNameId: Int, file: File): Flow<String> {

        val requestFile =
            File(file.path).asRequestBody(
                context.contentResolver.getType(file.toUri())
                    ?.toMediaTypeOrNull()
            )
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            File(file.path).name,
            requestFile
        )
        return kiosService.uploadProductImage(
            stockOpNameId = stockOpNameId.toString().toRequestBody(MultipartBody.FORM),
            file = filePart
        ).map { it.payload!! }
    }

    override suspend fun generateReport(stockOpNameId: Int): Flow<Report?> {
        return kiosService
            .generateReport(
                GenerateReportRequestDto(
                    stockOpNameId = stockOpNameId,
                    role = sessionManager.getUserRole().first(),
                    email = sessionManager.getUserEmail().first()
                )
            )
            .map { it.payload }
    }

    override suspend fun confirmReport(
        stockOPNameReportId: Int,
        totalAmountToBePaid: String,
        kiosOwnerSignURLFile: File,
        aeSignURLFile: File?,
        reportFile: File,
        KiosOwnerSignedBy: String,
        partialAmountConfirmedByAE: Boolean?,
        latitude: Double?,
        longitude: Double?,
    ): Flow<PaymentDetailDto?> {

        val requestKiosOwnerSignURLFile =
            File(kiosOwnerSignURLFile.path).asRequestBody(
                context.contentResolver.getType(kiosOwnerSignURLFile.toUri())
                    ?.toMediaTypeOrNull()
            )
        val kiosOwnerSignURLFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "kiosOwnerSignURLFile",
            File(kiosOwnerSignURLFile.path).name,
            requestKiosOwnerSignURLFile
        )

        val requestReportFile =
            File(reportFile.path).asRequestBody(
                context.contentResolver.getType(reportFile.toUri())
                    ?.toMediaTypeOrNull()
            )
        val reportFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "reportFile",
            File(reportFile.path).name,
            requestReportFile
        )

        var aeSignURLFilePart: MultipartBody.Part? = null
        if (aeSignURLFile != null) {
            val requestAeSignURLFile =
                File(aeSignURLFile.path).asRequestBody(
                    context.contentResolver.getType(aeSignURLFile.toUri())
                        ?.toMediaTypeOrNull()
                )
            aeSignURLFilePart = MultipartBody.Part.createFormData(
                "aeSignURLFile",
                File(aeSignURLFile.path).name,
                requestAeSignURLFile
            )
        }

        return kiosService.confirmReport(
            stockOPNameReportId = stockOPNameReportId.toString().toRequestBody(MultipartBody.FORM),
            totalAmountToBePaid = totalAmountToBePaid.toRequestBody(MultipartBody.FORM),
            aeId = sessionManager.getAeId().first().toString().toRequestBody(MultipartBody.FORM),
            kiosOwnerSignURLFile = kiosOwnerSignURLFilePart,
            aeSignURLFile = aeSignURLFilePart,
            reportFile = reportFilePart,
            KiosOwnerSignedBy = KiosOwnerSignedBy.toRequestBody(MultipartBody.FORM),
            role = sessionManager.getUserRole().first().toRequestBody(MultipartBody.FORM),
            partialAmountConfirmedByAE = partialAmountConfirmedByAE.toString()
                .toRequestBody(MultipartBody.FORM),
            latitude = latitude?.toString()?.toRequestBody(MultipartBody.FORM),
            longitude = longitude?.toString()?.toRequestBody(MultipartBody.FORM)
        ).map { it.payload }
    }

    override suspend fun cancelReport(
        stockOPNameReportId: Int,
        cancelReason: String,
        note: String
    ): Flow<Boolean> {
        val requestBody = CancelReportRequestDto(
            stockOPNameReportId = stockOPNameReportId,
            cancelReason = cancelReason,
            note = note,
            role = sessionManager.getUserRole().first()
        )
        return kiosService.cancelReport(requestBody).map {
            it.message == "Success"
        }
    }

    override suspend fun getCancelReasons(): Flow<List<CancelReasonDto>?> {
        return kiosService.getCancelReasons().map { it.payload }
    }

    override fun getKiosStocks(stockOpNameId: Int): Flow<KiosDetail?> {
        return kiosService.getKiosStocks(stockOpNameId).map { it.payload }
    }

    override fun getBtnStatus(stockOpNameId: Int): Flow<BtnStatusDto?> {
        return kiosService.getBtnStatus(stockOpNameId).map { it.payload }
    }

    override fun markEligibleForQa(stockOpNameId: Int): Flow<MarkEligibleForQaResponseDto?> {
        return kiosService.markEligibleForQa(MarkEligibleForQaRequestDto(stockOpNameId))
            .map { it.payload }
    }

    override fun getSkuProducts(kiosCode: String): Flow<List<SkuDTO>?> {
        return kiosService
            .getProducts(kiosCode)
            .map { it.payload }
    }

    override suspend fun getKiosData(): Flow<KiosData> {
        return kiosService.getKios(sessionManager.getAeId().first()).map { it.payload!! }
    }

    override fun verifyOtp(stockOPNameReportId: Int, otp: String): Flow<Report?> {
        return kiosService
            .completePayment(
                CompletePaymentRequestDto(
                    stockOPNameReportId = stockOPNameReportId,
                    otp = otp
                )
            )
            .map {
                it.payload
            }
    }

    override fun getPaymentDetails(stockOpNameId: Int): Flow<PaymentDetailDto?> {
        return kiosService
            .getPaymentDetails(MarkEligibleForQaRequestDto(stockOpNameId))
            .map { it.payload }
    }

    override fun getKioskResignForm(): Flow<KioskResignFormDto?> {
        return kiosService.getKioskResignForm().map { it.payload }
    }

    override fun resendKioskResignOtp(
        formId: Int,
        kioskCode: String
    ): Flow<KioskResignOtpDto?> {
        val request = ResendKioskResignOtpRequestDto(
            kioskCode = kioskCode,
            formId = formId,
            isRead = "false"
        )
        return kiosService.resendKioskResignOTP(request).map { it.payload }
    }

    override suspend fun submitKioskResignForm(
        formId: Int,
        kioskCode: String,
        responses: Map<String, List<ResignOption>>,
    ): Flow<KioskResignOtpDto?> {
        val request = SubmitKioskResignFormRequestDto(
            formId = formId,
            aeId = sessionManager.getAeId().first(),
            kioskCode = kioskCode,
            responses = responses
        )
        return kiosService.submitKioskResignForm(request).map { it.payload }
    }

    override fun verifyKioskResignOTP(
        kioskCode: String?,
        otp: String?,
        formId: Int?
    ): Flow<KioskResignOtpDto?> {
        return kiosService
            .verifyKioskResignForm(
                VerifyKioskResignFormRequestDto(
                    kioskCode = kioskCode.orEmpty(),
                    otp = otp.orEmpty(),
                    formId = formId ?: 0
                )
            )
            .map {
                it.payload
            }
    }

    override fun getKioskDetail(kioskCode: String?): Flow<KioskDetailDto?> {
        return kiosService.getKioskDetail(kioskCode).map { it.payload }
    }

    override fun getStockWithdrawalItems(stockTransferId: String): Flow<StockWithdrawalDto?> {
        return kiosService
            .getStockWithdrawalList(stockTransferId)
            .map {
                it.payload
            }
    }

    override fun submitStockWithdrawalOTP(
        aeEmail: String,
        stockTransferId: String,
        deliveryProof: File,
        otp: String,
    ): Flow<StockWithdrawalOTPDto?> {

        val dp =
            File(deliveryProof.path).asRequestBody(
                context.contentResolver.getType(deliveryProof.toUri())
                    ?.toMediaTypeOrNull()
            )

        val deliveryProofPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "deliveryProof",
            File(deliveryProof.path).name,
            dp
        )

        return kiosService
            .submitStockWithdrawalOTP(
                aeEmail.toRequestBody(MultipartBody.FORM),
                stockTransferId.toRequestBody(MultipartBody.FORM),
                otp.toRequestBody(MultipartBody.FORM),
                deliveryProofPart
            )
            .map {
                it
            }
    }
}