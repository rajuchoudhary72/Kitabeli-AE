package com.kitabeli.ae.data

import android.content.Context
import androidx.core.net.toUri
import com.kitabeli.ae.data.remote.dto.AddStockProductRequestDto
import com.kitabeli.ae.data.remote.dto.InitializeStockRequestDto
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.service.KiosService
import com.kitabeli.ae.model.repository.KiosRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
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
) : KiosRepository {

    override fun initializeStock(kiosCode: String): Flow<KiosDto> {
        return kiosService
            .initializeStock(
                InitializeStockRequestDto(
                    kiosCode = kiosCode,
                    aeId = "5"
                )
            )
            .map { it.payload }
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
            .map { it.payload }
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
        ).map { it.payload }
    }
}