package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.AddReplenishmentProductRequest
import com.kitabeli.ae.data.remote.dto.RefillRequestDto
import com.kitabeli.ae.data.remote.dto.ReturnProductDto
import com.kitabeli.ae.data.remote.dto.ReturnReasonDto
import com.kitabeli.ae.data.remote.dto.VerifyReturnRequestOtpDto
import kotlinx.coroutines.flow.Flow

interface ReplenishmentRepository {
    fun getReturnReasonList(): Flow<List<ReturnReasonDto>?>

    fun getReturnItemList(
        aeId: String,
        kioskCode: String
    ): Flow<List<ReturnProductDto>?>

    fun addReturnProduct(
        request: AddReplenishmentProductRequest
    ): Flow<String?>

    fun updateReturnProduct(
        itemId: Long?,
        request: AddReplenishmentProductRequest
    ): Flow<List<ReturnProductDto>?>

    fun deleteReturnProduct(
        itemId: Long?,
        aeId: String,
        kioskCode: String,
    ): Flow<List<ReturnProductDto>?>

    suspend fun createRefillRequest(
        kioskCode: String
    ): Flow<RefillRequestDto?>

    suspend fun createStockReturnRequest(
        refillRequestId: Long?
    ): Flow<RefillRequestDto?>

    fun verifyStockReturnRequestOtp(
        request: VerifyReturnRequestOtpDto
    ): Flow<String?>
}