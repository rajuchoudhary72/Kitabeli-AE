package com.kitabeli.ae.data

import android.content.Context
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.*
import com.kitabeli.ae.data.remote.service.ReplenishmentService
import com.kitabeli.ae.model.repository.ReplenishmentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReplenishmentRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val sessionManager: SessionManager,
    private val replenishmentService: ReplenishmentService
) : ReplenishmentRepository {
    override fun getReturnReasonList(): Flow<List<ReturnReasonDto>?> {
        return replenishmentService.getReturnReasonList().map { it.payload }
    }

    override fun getReturnItemList(refillRequestId: Long?): Flow<List<ReturnProductDto>?> {
        return replenishmentService.getReturnItemList(refillRequestId).map { it.payload }
    }

    override fun addReturnProduct(
        refillRequestId: Long?,
        request: AddReturnProductRequestDto
    ): Flow<String?> {
        return replenishmentService.addReturnProduct(refillRequestId, request).map { it.payload }
    }

    override fun updateReturnProduct(
        refillRequestId: Long?,
        itemId: Long?,
        request: AddReturnProductRequestDto
    ): Flow<List<ReturnProductDto>?> {
        return replenishmentService.updateReturnProduct(
            refillRequestId = refillRequestId,
            itemId = itemId,
            request = request
        ).map { it.payload }
    }

    override fun deleteReturnProduct(
        refillRequestId: Long?,
        itemId: Long?
    ): Flow<List<ReturnProductDto>?> {
        return replenishmentService.deleteReturnProduct(
            refillRequestId = refillRequestId,
            itemId = itemId
        ).map { it.payload }
    }

    override suspend fun createRefillRequest(
        kioskCode: String
    ): Flow<RefillRequestDto?> {
        val request = CreateRefillRequestDto(
            kioskCode = kioskCode,
            transferType = RETURN_TRF_TRANSFER_TYPE,
            transferStockStatus = INITIATED_STATUS,
            requester = sessionManager.getUserEmail().first()
        )
        return replenishmentService.createRefillRequest(request).map { it.payload }
    }

    override suspend fun createStockReturnRequest(
        refillRequestId: Long?
    ): Flow<RefillRequestDto?> {
        return replenishmentService.createStockReturnRequest(
            refillRequestId = refillRequestId,
            requester = sessionManager.getUserEmail().first()
        ).map { it.payload }
    }

    override fun verifyStockReturnRequestOtp(
        stockTransferId: String?,
        otp: String?
    ): Flow<String?> {
        val request = VerifyReturnRequestOtpDto(stockTransferId = stockTransferId, otp = otp)
        return replenishmentService.verifyStockReturnRequestOtp(request).map { it.payload }
    }

    companion object {
        const val RETURN_TRF_TRANSFER_TYPE = "RETURN_TRANSFER"
        const val INITIATED_STATUS = "INITIATED"
    }
}