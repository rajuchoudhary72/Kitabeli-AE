package com.kitabeli.ae.data.remote.service


import com.kitabeli.ae.BuildConfig
import com.kitabeli.ae.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface ReplenishmentService {
    @GET("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/reason")
    fun getReturnReasonList(): Flow<BaseResponseDto<List<ReturnReasonDto>>>

    @GET("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/{refillRequestId}")
    fun getReturnItemList(
        @Path("refillRequestId") refillRequestId: Long?
    ): Flow<BaseResponseDto<List<ReturnProductDto>>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/{refillRequestId}/item")
    fun addReturnProduct(
        @Path("refillRequestId") refillRequestId: Long?,
        @Body request: AddReturnProductRequestDto
    ): Flow<BaseResponseDto<String>>

    @PUT("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/{refillRequestId}/item/{itemId}")
    fun updateReturnProduct(
        @Path("refillRequestId") refillRequestId: Long?,
        @Path("itemId") itemId: Long?,
        @Body request: AddReturnProductRequestDto
    ): Flow<BaseResponseDto<List<ReturnProductDto>>>

    @DELETE("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/refill-request/{refillRequestId}/item/{itemId}")
    fun deleteReturnProduct(
        @Path("refillRequestId") refillRequestId: Long?,
        @Path("itemId") itemId: Long?,
    ): Flow<BaseResponseDto<List<ReturnProductDto>>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/refill-request/create")
    fun createRefillRequest(
        @Body request: CreateRefillRequestDto
    ): Flow<BaseResponseDto<RefillRequestDto>>

    @POST("${BuildConfig.BASE_REPLENISHMENT_URL}api/v1/replenishment/refill-request/stock-request/{refillRequestId}")
    fun createStockReturnRequest(
        @Path("refillRequestId") refillRequestId: Long?,
        @Query("requester") requester: String
    ): Flow<BaseResponseDto<RefillRequestDto>>

    @POST("${BuildConfig.BASE_PO_URL}api/v1/purchase-order/transfer-stock/kios/confirm-return")
    fun verifyStockReturnRequestOtp(
        @Body request: VerifyReturnRequestOtpDto
    ): Flow<BaseResponseDto<String>>
}