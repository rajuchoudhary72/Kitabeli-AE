package com.kitabeli.ae.data

import android.content.Context
import androidx.core.net.toUri
import com.kitabeli.ae.data.local.SessionManager
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
                    aeId = sessionManager.getAeId().first().toString()
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

    override fun generateReport(stockOpNameId: Int): Flow<Report?> {
        return kiosService
            .generateReport(GenerateReportRequestDto(stockOpNameId = stockOpNameId))
            .map { it.payload }
    }

    override suspend fun confirmReport(
        stockOPNameReportId: Int,
        totalAmountToBePaid: String,
        kiosOwnerSignURLFile: File,
        aeSignURLFile: File,
        reportFile: File,
        KiosOwnerSignedBy: String
    ): Flow<Report?> {


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


        val requestAeSignURLFile =
            File(aeSignURLFile.path).asRequestBody(
                context.contentResolver.getType(aeSignURLFile.toUri())
                    ?.toMediaTypeOrNull()
            )
        val aeSignURLFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "aeSignURLFile",
            File(aeSignURLFile.path).name,
            requestAeSignURLFile
        )


        return kiosService.confirmReport(
            stockOPNameReportId = stockOPNameReportId.toString().toRequestBody(MultipartBody.FORM),
            totalAmountToBePaid = totalAmountToBePaid.toString().toRequestBody(MultipartBody.FORM),
            aeId = sessionManager.getAeId().first().toString().toRequestBody(MultipartBody.FORM),
            kiosOwnerSignURLFile = kiosOwnerSignURLFilePart,
            aeSignURLFile = aeSignURLFilePart,
            reportFile = reportFilePart,
            KiosOwnerSignedBy = KiosOwnerSignedBy.toRequestBody(MultipartBody.FORM),
        ).map { it.payload }
    }

    override suspend fun cancelReport(stockOPNameReportId: Int): Flow<Boolean> {
        return kiosService.cancelReport(CancelReportRequestDto(stockOPNameReportId)).map {
            it.message == "Success"
        }
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
}