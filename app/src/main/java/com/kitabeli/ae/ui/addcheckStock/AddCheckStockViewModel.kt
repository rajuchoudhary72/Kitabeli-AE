package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddCheckStockViewModel @Inject constructor() : ViewModel() {

    private val _mirtaSignature = MutableStateFlow<Bitmap?>(null)
    val mirtaSignature = _mirtaSignature.asStateFlow()

    private val _aeSignature = MutableStateFlow<Bitmap?>(null)
    val aeSignature = _aeSignature.asStateFlow()


    fun setMitraSignature(bitmap: Bitmap) {
        _mirtaSignature.update { bitmap }
    }

    fun setAeSignature(bitmap: Bitmap) {
        _aeSignature.update { bitmap }
    }
}