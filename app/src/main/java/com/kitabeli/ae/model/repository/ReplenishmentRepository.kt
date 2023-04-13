package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.AddReturnProductRequestDto
import com.kitabeli.ae.data.remote.dto.RefillRequestDto
import com.kitabeli.ae.data.remote.dto.ReturnProductDto
import com.kitabeli.ae.data.remote.dto.ReturnReasonDto
import kotlinx.coroutines.flow.Flow

interface ReplenishmentRepository {
    fun getReturnReasonList(): Flow<List<ReturnReasonDto>?>

    fun getReturnItemList(
        refillRequestId: Long?
    ): Flow<List<ReturnProductDto>?>

    fun addReturnProduct(
        refillRequestId: Long?,
        request: AddReturnProductRequestDto
    ): Flow<String?>

    fun updateReturnProduct(
        refillRequestId: Long?,
        itemId: Long?,
        request: AddReturnProductRequestDto
    ): Flow<List<ReturnProductDto>?>

    fun deleteReturnProduct(
        refillRequestId: Long?,
        itemId: Long?,
    ): Flow<List<ReturnProductDto>?>

    suspend fun createRefillRequest(
        kioskCode: String
    ): Flow<RefillRequestDto?>

    suspend fun createStockReturnRequest(
        refillRequestId: Long?
    ): Flow<RefillRequestDto?>

    fun verifyStockReturnRequestOtp(
        stockTransferId: String?,
        otp: String?,
    ): Flow<String?>
}