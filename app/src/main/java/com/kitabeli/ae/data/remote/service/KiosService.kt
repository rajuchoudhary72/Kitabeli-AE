package com.kitabeli.ae.data.remote.service

import com.kitabeli.ae.data.remote.dto.AddStockProductRequestDto
import com.kitabeli.ae.data.remote.dto.BaseResponseDto
import com.kitabeli.ae.data.remote.dto.InitializeStockRequestDto
import com.kitabeli.ae.data.remote.dto.KiosDto
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface KiosService {

    @POST("api/v1/stock-opname/initialize")
    fun initializeStock(@Body requestDto: InitializeStockRequestDto): Flow<BaseResponseDto<KiosDto>>


    @POST("api/v1/stock-opname/item/add")
    fun addStockProduct(@Body requestDto: AddStockProductRequestDto): Flow<BaseResponseDto<KiosDto>>


    @Multipart
    @POST("api/v1/stock-opname/item/img-proof/upload")
    fun uploadProductImage(
        @Part("stockOpnameId") stockOpNameId: RequestBody,
        @Part file: MultipartBody.Part,
    ): Flow<BaseResponseDto<String>>

}