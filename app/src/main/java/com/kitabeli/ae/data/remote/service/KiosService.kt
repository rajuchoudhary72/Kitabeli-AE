package com.kitabeli.ae.data.remote.service

import com.kitabeli.ae.BuildConfig
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

    @PUT("api/v1/stock-opname/item/{stockOpnameItemId}")
    fun updateStockProduct(
        @Path("stockOpnameItemId") stockOpnameItemId: String,
        @Body requestDto: AddStockProductRequestDto
    ): Flow<BaseResponseDto<KiosDto>>

    @Multipart
    @POST("api/v1/stock-opname/item/img-proof/upload")
    fun uploadProductImage(
        @Part("stockOpnameId") stockOpNameId: RequestBody,
        @Part file: MultipartBody.Part,
    ): Flow<BaseResponseDto<String>>


    @POST("api/v2/stock-opname/report")
    fun generateReport(@Body requestDto: GenerateReportRequestDto): Flow<BaseResponseDto<Report>>

    @Multipart
    @POST("api/v1/stock-opname/report/confirm")
    fun confirmReport(
        @Part("stockOPNameReportId") stockOPNameReportId: RequestBody,
        @Part("totalAmountToBePaid") totalAmountToBePaid: RequestBody,
        @Part("aeId") aeId: RequestBody,
        @Part kiosOwnerSignURLFile: MultipartBody.Part,
        @Part aeSignURLFile: MultipartBody.Part?,
        @Part reportFile: MultipartBody.Part,
        @Part("KiosOwnerSignedBy") KiosOwnerSignedBy: RequestBody,
        @Part("role") role: RequestBody,
        @Part("partialAmountConfirmedByAE") partialAmountConfirmedByAE: RequestBody,
        @Part("latitude") latitude: RequestBody? = null,
        @Part("longitude") longitude: RequestBody? = null,
    ): Flow<BaseResponseDto<PaymentDetailDto>>

    @POST("api/v2/stock-opname/report/complete-payment")
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

    @POST("api/v1/stock-opname/report/payment/details")
    fun getPaymentDetails(
        @Body request: MarkEligibleForQaRequestDto
    ): Flow<BaseResponseDto<PaymentDetailDto>>

    @GET("${BuildConfig.BASE_KIOSK_PATH}api/v1/kiosk-form/RESIGN-FORM")
    fun getKioskResignForm(): Flow<BaseResponseDto<KioskResignFormDto>>

    @POST("${BuildConfig.BASE_KIOSK_PATH}api/v1/kiosk-form/submit-response")
    fun submitKioskResignForm(
        @Body request: SubmitKioskResignFormRequestDto
    ): Flow<BaseResponseDto<KioskResignOtpDto>>

    @POST("${BuildConfig.BASE_KIOSK_PATH}api/v1/kiosk-form/verify-form")
    fun verifyKioskResignForm(
        @Body request: VerifyKioskResignFormRequestDto
    ): Flow<BaseResponseDto<KioskResignOtpDto>>

    @GET("${BuildConfig.BASE_KIOSK_PATH}api/v1/kiosk")
    fun getKioskDetail(
        @Query("kioskCode") kioskCode: String?
    ): Flow<BaseResponseDto<KioskDetailDto>>

    @POST("${BuildConfig.BASE_KIOSK_PATH}api/v1/kiosk-form/otp/update-read")
    fun resendKioskResignOTP(
        @Body request: ResendKioskResignOtpRequestDto
    ): Flow<BaseResponseDto<KioskResignOtpDto>>

    @GET("${BuildConfig.BASE_PO_URL}api/v1/purchase-order/transfer-stock/get-stock-transfer-items")
    fun getStockWithdrawalList(
        @Query("transactionId") transactionId: String
    ): Flow<BaseResponseDto<StockWithdrawalDto>>

    @Multipart
    @POST("${BuildConfig.DRIVER_BASE_URL}api/v1/shipments/ae/complete-shipment")
    fun submitStockWithdrawalOTP(
        @Part("aeEmail") stockOPNameReportId: RequestBody,
        @Part("stockTransferId") totalAmountToBePaid: RequestBody,
        @Part("otp") KiosOwnerSignedBy: RequestBody,
        @Part deliveryProof: MultipartBody.Part
    ): Flow<StockWithdrawalOTPDto>
}