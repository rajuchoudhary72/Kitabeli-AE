package com.kitabeli.ae.data.remote.service

import com.kitabeli.ae.data.remote.dto.AddStockProductRequestDto
import com.kitabeli.ae.data.remote.dto.BaseResponseDto
import com.kitabeli.ae.data.remote.dto.BtnStatusDto
import com.kitabeli.ae.data.remote.dto.CompletePaymentRequestDto
import com.kitabeli.ae.data.remote.dto.GenerateReportRequestDto
import com.kitabeli.ae.data.remote.dto.InitializeStockRequestDto
import com.kitabeli.ae.data.remote.dto.KiosData
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.dto.MarkEligibleForQaRequestDto
import com.kitabeli.ae.data.remote.dto.MarkEligibleForQaResponseDto
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.data.remote.dto.SkuDTO
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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


    @POST("api/v1/stock-opname/report")
    fun generateReport(@Body requestDto: GenerateReportRequestDto): Flow<BaseResponseDto<Report>>

    @Multipart
    @POST("api/v1/stock-opname/report/confirm")
    fun confirmReport(
        @Part("stockOPNameReportId") stockOPNameReportId: RequestBody,
        @Part("totalAmountToBePaid") totalAmountToBePaid: RequestBody,
        @Part("aeId") aeId: RequestBody,
        @Part kiosOwnerSignURLFile: MultipartBody.Part,
        @Part aeSignURLFile: MultipartBody.Part,
        @Part reportFile: MultipartBody.Part,
    ): Flow<BaseResponseDto<Report>>

    @POST("api/v1/stock-opname/report/complete-payment")
    fun completePayment(@Body requestDto: CompletePaymentRequestDto): Flow<BaseResponseDto<Report>>

    @GET("api/v1/stock-opname/{stockOpNameId}")
    fun getKiosStocks(@Path("stockOpNameId") stockOpNameId: Int): Flow<BaseResponseDto<KiosDetail>>

    @GET("api/v1/stock-opname/btn-state")
    fun getBtnStatus(@Query("stockOpnameId") stockOpNameId: Int): Flow<BaseResponseDto<BtnStatusDto>>

    @GET("api/v1/stock-opname/item/list")
    fun getProducts(@Query("kioskCode") kioskCode: String): Flow<BaseResponseDto<List<SkuDTO>>>

    @GET("api/v1/stock-opname/")
    fun getKios(@Query("aeId") aeId: Int): Flow<BaseResponseDto<KiosData>>

    @POST("api/v1/stock-opname/mark-eligible-qa")
    fun markEligibleForQa(@Body request: MarkEligibleForQaRequestDto): Flow<BaseResponseDto<MarkEligibleForQaResponseDto>>

}