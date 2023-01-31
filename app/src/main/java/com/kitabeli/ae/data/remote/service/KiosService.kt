package com.kitabeli.ae.data.remote.service

import com.kitabeli.ae.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

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
        @Part("KiosOwnerSignedBy") KiosOwnerSignedBy: RequestBody,
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

    @POST("api/v1/stock-opname/report/cancel")
    fun cancelReport(@Body cancelReportRequestDto: CancelReportRequestDto): Flow<BaseResponseDto<Report>>

    @Headers("isAuthorizable: false")
    @GET("https://api.kitabeli.id/api/v1/mitra/training-tab/ae")
    fun getTrainingVideos(): Flow<TrainingVideoDto>

    @GET("api/v1/stock-opname/report/cancel-reason")
    fun getCancelReasons(): Flow<BaseResponseDto<List<CancelReasonDto>>>
}