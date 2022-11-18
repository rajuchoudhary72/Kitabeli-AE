package com.kitabeli.ae.ui.kios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kitabeli.ae.data.remote.dto.KiosDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KiosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _kios = savedStateHandle.get<KiosDto>("kios")
    val kios = _kios
}