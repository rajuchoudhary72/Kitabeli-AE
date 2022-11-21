package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddCheckStockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : ViewModel() {

    private val _retry = MutableStateFlow(false)

    private val stockOpNameId = savedStateHandle.get<Int>("stockOpNameId")
        ?: throw RuntimeException("stockOpNameId requried, pass stockOpNameId in fragment argumnets.")

    val tncAgree = MutableStateFlow(false)

    private val _report = _retry
        .flatMapLatest {
            kiosRepository.generateReport(stockOpNameId).flowOn(Dispatchers.IO)
        }.toLoadingState()

    val uiState = combine(
        flow = _report,
        flow2 = tncAgree
    ) { report, _ ->
        when (report) {
            LoadState.Loading -> {
                UiState.Loading
            }

            is LoadState.Error -> {
                UiState.Error(report.getAppErrorIfExists())
            }

            is LoadState.Loaded -> {
                UiState.Success(
                    report = report.value
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


    val isSuccess = uiState.map { it is UiState.Success }.asLiveData()

    val isLoading = uiState.map { it is UiState.Loading }.asLiveData()

    val error: LiveData<AppError?> =
        uiState.map { if (it is UiState.Error) it.error else null }.asLiveData()


    private val _mirtaSignature = MutableStateFlow<Bitmap?>(null)
    val mirtaSignature = _mirtaSignature.asStateFlow()

    private val _aeSignature = MutableStateFlow<Bitmap?>(null)
    val aeSignature = _aeSignature.asStateFlow()


    fun retry() {
        _retry.update { it.not() }
    }


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


