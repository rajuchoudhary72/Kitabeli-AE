package com.kitabeli.ae.ui.addproduct

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet.Companion.KIOS_CODE
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet.Companion.STOCK_OP_NAME_ID
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _stockOpNameId = savedStateHandle.getStateFlow(STOCK_OP_NAME_ID, 0)
    private val _kiosCode = savedStateHandle.getStateFlow(KIOS_CODE, "")


    private val _products =
        _kiosCode.flatMapLatest { kiosRepository.getSkuProducts(it) }.flowOn(Dispatchers.IO)
            .toLoadingState()
    val products = _products

    val stockCount = MutableStateFlow("0")

    val photoProof = MutableStateFlow("")


    fun increaseStockCount() {
        stockCount.update { it.toInt().plus(1).toString() }
    }

    fun decreaseStockCount() {
        stockCount.update {
            if (it == "0")
                it
            else
                it.toInt().minus(1).toString()
        }
    }

    fun uploadProductImage(file: File) {
        viewModelScope.launch {
            kiosRepository.uploadProductImage(
                stockOpNameId = _stockOpNameId.value,
                imageFile = file
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let { photoUrl ->
                        photoProof.update { photoUrl }
                    }
                }
        }
    }


    fun addProduct(func: () -> Unit) {
        viewModelScope.launch {
            kiosRepository.addStockProduct(
                stockOpNameId = _stockOpNameId.value,
                photoProof = photoProof.value,
                stockCount = stockCount.value.toInt(),
                skuName = "Rice 1kg",
                skuId = 456
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let { photoUrl ->
                        func()
                    }
                }
        }
    }


}