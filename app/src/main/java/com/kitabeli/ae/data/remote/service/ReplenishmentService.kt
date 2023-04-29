package com.kitabeli.ae.data.remote.service


import com.kitabeli.ae.BuildConfig
import com.kitabeli.ae.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface ReplenishmentService {
    @GET("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/reason")
    fun getReturnReasonList(): Flow<BaseResponseDto<List<ReturnReasonDto>>>

    @GET("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/ae/refill-request")
    fun getReturnItemList(
        @Query("aeId") aeId: String,
        @Query("kioskCode") kioskCode: String,
    ): Flow<BaseResponseDto<ReturnProductResponseDto>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/ae/refill-request/item")
    fun addReturnProduct(
        @Body request: AddReplenishmentProductRequest
    ): Flow<BaseResponseDto<ReturnProductResponseDto>>

    @PUT("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/ae/refill-request/item/{itemId}")
    fun updateReturnProduct(
        @Path("itemId") itemId: Long?,
        @Body request: AddReplenishmentProductRequest
    ): Flow<BaseResponseDto<ReturnProductResponseDto>>

    @DELETE("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/ae/refill-request/item/{itemId}")
    fun deleteReturnProduct(
        @Path("itemId") itemId: Long?,
        @Query("aeId") aeId: String,
        @Query("kioskCode") kioskCode: String,
    ): Flow<BaseResponseDto<ReturnProductResponseDto>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/refill-request/create")
    fun createRefillRequest(
        @Body request: CreateRefillRequestDto
    ): Flow<BaseResponseDto<RefillRequestDto>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/refill-request/stock-request/{refillRequestId}")
    fun createStockReturnRequest(
        @Path("refillRequestId") refillRequestId: Long?,
        @Query("requester") requester: String
    ): Flow<BaseResponseDto<RefillRequestDto>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/ae/refill-request/verify-otp")
    fun verifyStockReturnRequestOtp(
        @Body request: VerifyReturnRequestOtpDto
    ): Flow<BaseResponseDto<String>>
}