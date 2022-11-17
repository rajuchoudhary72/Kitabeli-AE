package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddCheckStockViewModel @Inject constructor() : ViewModel() {

    val tncAgree = MutableStateFlow(false)

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

    val enableButton = combine(
        flow = tncAgree,
        flow2 = mirtaSignature,
        flow3 = aeSignature
    ) { tncAgree, mirtaSignature, aeSignature ->
        tncAgree && mirtaSignature != null && aeSignature != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    ).asLiveData()
}