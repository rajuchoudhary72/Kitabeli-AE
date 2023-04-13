package com.kitabeli.ae.ui.resign_and_return

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.*
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.model.repository.ReplenishmentRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.ui.resign_and_return.return_request.ReturnRequestProductAdapter.Companion.EXPIRED_ITEM
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ResignAndReturnViewModel @Inject constructor(
    private val kioskRepository: KiosRepository,
    private val replenishmentRepository: ReplenishmentRepository,
) : BaseViewModel() {

    private val _resignForm: MutableLiveData<UiState> = MutableLiveData()
    val resignForm: LiveData<UiState> = _resignForm

    private val _isFormSubmitted: MutableLiveData<Boolean> = MutableLiveData(null)
    val isFormSubmitted: LiveData<Boolean> = _isFormSubmitted

    private val _isOtpResent: MutableLiveData<Boolean> = MutableLiveData(null)
    val isOtpResent: LiveData<Boolean> = _isOtpResent

    private val _productList: MutableLiveData<List<SkuDTO>> = MutableLiveData()
    val productList: LiveData<List<SkuDTO>> = _productList

    private val _reasonList: MutableLiveData<List<ReturnReasonDto>> = MutableLiveData()
    val reasonList: LiveData<List<ReturnReasonDto>> = _reasonList

    private val _refillRequest: MutableLiveData<RefillRequestDto?> = MutableLiveData(null)
    val refillRequest: LiveData<RefillRequestDto?> = _refillRequest

    private val _returnItemList: MutableLiveData<List<ReturnProductDto>?> = MutableLiveData(null)
    val returnItemList: LiveData<List<ReturnProductDto>?> = _returnItemList

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

    var selectedProduct: SkuDTO? = null
    var selectedExpiryDate: Date? = null
    var selectedReason: ReturnReasonDto? = null
    var stockQty: Int? = null

    fun getKioskResignForm() = viewModelScope.launch {
        kioskRepository.getKioskResignForm()
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _resignForm.postValue(UiState.Success(resignForm = response.value))
                } else {
                    _resignForm.postValue(UiState.Error("Failed"))
                }
            }
    }

    fun submitKioskResignForm(
        formId: Int,
        kioskCode: String,
        responses: Map<String, List<ResignOption>>,
    ) = viewModelScope.launch {
        kioskRepository.submitKioskResignForm(
            formId = formId,
            kioskCode = kioskCode,
            responses = responses
        )
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _isFormSubmitted.postValue(true)
                } else {
                    _isFormSubmitted.postValue(false)
                }
            }
    }

    fun resendKioskResignOtp(
        formId: Int,
        kioskCode: String
    ) = viewModelScope.launch {
        kioskRepository.resendKioskResignOtp(
            formId = formId,
            kioskCode = kioskCode
        )
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _isOtpResent.postValue(true)
                } else {
                    _isOtpResent.postValue(false)
                }
            }
    }

    fun getSkuList(kioskCode: String?) = viewModelScope.launch {
        kioskRepository.getSkuProducts(kioskCode.orEmpty())
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _productList.postValue(response.value.orEmpty())
                }
            }
    }

    fun getReturnReasonList() = viewModelScope.launch {
        replenishmentRepository.getReturnReasonList()
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _reasonList.postValue(response.value.orEmpty())
                }
            }
    }

    fun createRefillRequest(kioskCode: String) = viewModelScope.launch {
        replenishmentRepository.createRefillRequest(kioskCode)
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    val result = response.value
                    _refillRequest.postValue(result)
                }
            }
    }

    fun getReturnRequestItemList(refillRequestId: Long?) = viewModelScope.launch {
        replenishmentRepository.getReturnItemList(refillRequestId)
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    _returnItemList.postValue(response.value)
                } else {
                    _returnItemList.postValue(emptyList())
                }
            }
    }

    fun addReturnRequestProduct(
        refillRequestId: Long?,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        val request = AddReturnProductRequestDto(
            returnItemDTO = listOf(
                ReturnProductRequestDto(
                    itemId = selectedProduct?.skuId?.toLong(),
                    requestQuantity = stockQty,
                    reason = selectedReason?.name,
                    details = when {
                        selectedReason?.name == EXPIRED_ITEM && selectedExpiryDate != null -> {
                            dateFormatter.format(selectedExpiryDate!!)
                        }
                        else -> null
                    }
                )
            )
        )
        replenishmentRepository.addReturnProduct(refillRequestId, request)
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    onSuccess.invoke()
                }
            }
    }

    fun updateReturnRequestProduct(
        refillRequestId: Long?,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        val request = AddReturnProductRequestDto(
            returnItemDTO = listOf(
                ReturnProductRequestDto(
                    itemId = selectedProduct?.skuId?.toLong(),
                    requestQuantity = stockQty,
                    reason = selectedReason?.name,
                    details = when {
                        selectedReason?.name == EXPIRED_ITEM && selectedExpiryDate != null -> {
                            dateFormatter.format(selectedExpiryDate!!)
                        }
                        else -> null
                    }
                )
            )
        )
        replenishmentRepository.updateReturnProduct(
            refillRequestId = refillRequestId,
            itemId = selectedProduct?.skuId?.toLong(),
            request = request
        )
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    onSuccess.invoke()
                }
            }
    }

    fun deleteReturnRequestProduct(
        refillRequestId: Long?,
        itemId: Long?,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        replenishmentRepository.deleteReturnProduct(
            refillRequestId = refillRequestId,
            itemId = itemId
        )
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    onSuccess.invoke()
                }
            }
    }

    fun createStockReturnRequest(
        refillRequestId: Long?,
        onSuccess: (String) -> Unit
    ) = viewModelScope.launch {
        replenishmentRepository.createStockReturnRequest(refillRequestId)
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    onSuccess.invoke(response.value?.stockTransferId.orEmpty())
                }
            }
    }
}