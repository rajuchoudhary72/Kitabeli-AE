package com.kitabeli.ae.ui.addcheckStock

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddCheckStockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _retry = MutableStateFlow(false)

    private val stockOpNameId = savedStateHandle.get<Int>("stockOpNameId")
        ?: throw RuntimeException("stockOpNameId required, pass stockOpNameId in fragment arguments.")
    val tncAgree = MutableStateFlow(false)
    val statusMitraName = MutableStateFlow(true)
    val enterdMitraname = MutableStateFlow("")

    private val _report = _retry
        .flatMapLatest {
            kiosRepository.generateReport(stockOpNameId).flowOn(Dispatchers.IO)
        }.toLoadingState()

    val uiState = combine(
        flow = _report,
        flow2 = tncAgree
    ) { report: LoadState<Report?>, _ ->
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
    val kiosCode =
        uiState.map { if (it is UiState.Success) it.report?.kioskDTO?.kioskCode ?: "" else "" }
            .asLiveData()

    val aeName =
        uiState.map {
            if (it is UiState.Success) it.report?.accountExecutiveDTO?.legalName ?: "" else ""
        }
            .asLiveData()

    val mitraName =
        uiState.map { if (it is UiState.Success) it.report?.kioskDTO?.mitraName ?: "" else "" }
            .asLiveData()


    val isLoading = uiState.map { it is UiState.Loading }.asLiveData()

    val error: LiveData<AppError?> =
        uiState.map { if (it is UiState.Error) it.error else null }.asLiveData()

    private val _reportFile = MutableStateFlow<File?>(null)
    val reportFile = _reportFile.asStateFlow()

    private val _mirtaSignature = MutableStateFlow<File?>(null)
    val mirtaSignature = _mirtaSignature.asStateFlow()

    private val _aeSignature = MutableStateFlow<File?>(null)
    val aeSignature = _aeSignature.asStateFlow()


    fun retry() {
        _retry.update { it.not() }
    }


    fun setMitraSignature(file: File) {
        _mirtaSignature.update { file }
    }

    fun setAeSignature(file: File) {
        _aeSignature.update { file }
    }

    fun setReportFile(file: File) {
        _reportFile.update { file }
    }

    fun submitReport(func: () -> Unit) {
        viewModelScope.launch {
            delay(100)
            val report = (uiState.value as UiState.Success).report!!
            kiosRepository.confirmReport(
                stockOPNameReportId = report.id,
                totalAmountToBePaid = report.totalAmountToBePaid!!,
                aeSignURLFile = aeSignature.value!!,
                kiosOwnerSignURLFile = mirtaSignature.value!!,
                reportFile = reportFile.value!!,
                KiosOwnerSignedBy = enterdMitraname.value
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke()
                    }
                }
        }
    }

    fun verifyOtp(otp: String, func: () -> Unit) {

        viewModelScope.launch {
            val report = (uiState.value as UiState.Success).report!!
            kiosRepository.verifyOtp(
                stockOPNameReportId = report.id,
                otp = otp
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()

                    if (state is LoadState.Loaded) {
                        func.invoke()
                    }

                }
        }

    }

    fun cancelReport(func: () -> Unit) {
        viewModelScope.launch {
            val report = (uiState.value as UiState.Success).report!!
            kiosRepository.cancelReport(
                stockOPNameReportId = report.id,
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke()
                    }
                }
        }
    }

    val enableButton = combine(
        flow = tncAgree,
        flow2 = mirtaSignature,
        flow3 = aeSignature,
        flow4 = statusMitraName
    ) { tncAgree, mirtaSignature, aeSignature, statusMitraName ->
        tncAgree && statusMitraName && mirtaSignature != null && aeSignature != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    ).asLiveData()
}


