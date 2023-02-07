package com.kitabeli.ae.ui.addproduct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.SkuDTO
import com.kitabeli.ae.data.remote.dto.StockOpNameItemDTOS
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet.Companion.KIOS_CODE
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet.Companion.STOCK_OP_NAME_ID
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet.Companion.STOCK_PRODUCT
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.combine
import com.kitabeli.ae.utils.ext.requireValue
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val selectedProduct = savedStateHandle.get<StockOpNameItemDTOS>(STOCK_PRODUCT)

    val canSelectProduct = selectedProduct == null

    private val _stockOpNameId = savedStateHandle.getStateFlow(STOCK_OP_NAME_ID, 0)
    private val _kiosCode = savedStateHandle.getStateFlow(KIOS_CODE, "")

    val productName = MutableLiveData(selectedProduct?.skuName ?: "")

    private val _products =
        _kiosCode
            .flatMapLatest { kiosRepository.getSkuProducts(it) }
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .asLiveData()
    val products = _products

    private val productSku = productName.map { productName ->
        products.value?.getValueOrNull()?.firstOrNull { it.name == productName }
            ?: SkuDTO(skuId = -1, name = "")
    }

    val stockCount = MutableLiveData(selectedProduct?.stockCount?.toString() ?: "0")

    val photoProof = MutableLiveData(selectedProduct?.photoProof ?: "")

    val isAllDataFiled = combine(
        false,
        liveData1 = stockCount,
        liveData2 = photoProof,
        liveData3 = productSku
    ) { _: Boolean, count: String, proof: String, sku: SkuDTO ->
        count.isNotEmpty() && proof.isNotEmpty() && sku.skuId != -1
    }


    fun increaseStockCount() {
        stockCount.value = stockCount.value?.toInt()?.plus(1).toString()
    }

    fun decreaseStockCount() {
        val currentValue = stockCount.value

        stockCount.value =
            if (currentValue.isNullOrEmpty() || currentValue == "0")
                "0"
            else
                currentValue.toInt().minus(1).toString()
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
                        photoProof.value = photoUrl
                    }
                }
        }
    }


    fun addProduct(func: () -> Unit) {
        viewModelScope.launch {
            val skuItem = productSku.value
            kiosRepository.addStockProduct(
                stockOpNameId = _stockOpNameId.value,
                photoProof = photoProof.requireValue(),
                stockCount = stockCount.requireValue().toInt(),
                skuName = skuItem?.name ?: "",
                skuId = skuItem?.skuId ?: 2
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

    fun updateProduct(func: () -> Unit) {
        viewModelScope.launch {
            val skuItem = productSku.value
            kiosRepository.updateStockProduct(
                stockOpNameId = _stockOpNameId.value,
                photoProof = photoProof.requireValue(),
                stockCount = stockCount.requireValue().toInt(),
                skuName = skuItem?.name ?: "",
                skuId = skuItem?.skuId ?: 2
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