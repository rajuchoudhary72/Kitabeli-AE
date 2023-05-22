package com.kitabeli.ae.ui.addcheckStock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.PaymentDetailDto
import com.kitabeli.ae.data.remote.dto.Report
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.ErrorData
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toApiError
import com.kitabeli.ae.utils.ext.toAppError
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
    private val kiosRepository: KiosRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel() {

    private val _isKioskOwner = sessionManager.isKioskOwner()
    val isKioskOwner = _isKioskOwner

    private val _kioskCode = sessionManager.getKioskCode()
    val kioskCode = _kioskCode

    private val _retry = MutableStateFlow(false)

    private val _stockOpNameId = savedStateHandle.get<Int>("stockOpNameId")
        ?: throw RuntimeException("stockOpNameId required, pass stockOpNameId in fragment arguments.")
    val stockOpNameId = _stockOpNameId
    val tncAgree = MutableStateFlow(false)
    val statusMitraName = MutableStateFlow(true)
    val enteredMitraName = MutableStateFlow("")
    val isKioskOwnerUser = MutableStateFlow(false)
    val showResumePaymentBtn = MutableStateFlow(false)
    val agreementText = MutableStateFlow("")
    val showPaymentDialog = MutableLiveData(false)
    val partialAmountConfirmedByAE = MutableLiveData(false)
    val latitude: MutableLiveData<Double?> = MutableLiveData(null)
    val longitude: MutableLiveData<Double?> = MutableLiveData(null)

    private val _report = _retry
        .flatMapLatest {
            kiosRepository.generateReport(stockOpNameId).flowOn(Dispatchers.IO)
        }.toLoadingState()

    val uiState = combine(
        flow = _report,
        flow2 = tncAgree,
    ) { report: LoadState<Report?>, _ ->
        when (report) {
            LoadState.Loading -> {
                UiState.Loading
            }

            is LoadState.Error -> {
                val appError = report.getAppErrorIfExists()
                if (getErrorMessage(appError) == NO_BANK_ACCOUNT_ERROR) {
                    UiState.Success(
                        report = null,
                        shouldShowBankAlert = true
                    )
                } else {
                    UiState.Error(appError)
                }
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

    fun submitReport(
        openOTP: ((String?) -> Unit)? = null,
        openKiosShutDownDialog: ((PaymentDetailDto?) -> Unit)? = null,
        openPartialPaymentDialog: ((PaymentDetailDto?) -> Unit)? = null,
        openErrorDialog: ((ErrorData) -> Unit)? = null
    ) {
        viewModelScope.launch {
            delay(100)
            val report = (uiState.value as UiState.Success).report!!
            kiosRepository.confirmReport(
                stockOPNameReportId = report.id,
                totalAmountToBePaid = report.totalAmountToBePaid!!,
                aeSignURLFile = aeSignature.value,
                kiosOwnerSignURLFile = mirtaSignature.value!!,
                reportFile = reportFile.value!!,
                KiosOwnerSignedBy = enteredMitraName.value,
                partialAmountConfirmedByAE = partialAmountConfirmedByAE.value,
                latitude = latitude.value,
                longitude = longitude.value
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.getAppErrorIfExists()?.toAppError()?.let { appError ->
                        if (appError is AppError.ApiException.BadRequestException) {
                            val apiError = appError.toApiError()

                            if (apiError.code == "1.ID") {
                                apiError.payload?.let { openErrorDialog?.invoke(it) }
                            } else {
                                onError(appError)
                            }
                        } else {
                            onError(appError)
                        }
                    }

                    showLoading(state.isLoading)
                    if (state is LoadState.Loaded) {
                        val data = state.value
                        data?.let { model ->
                            if (partialAmountConfirmedByAE.value == true) {
                                openOTP?.invoke(model.token)
                            } else {
                                if (model.is_kiosk_shutdown == true && model.orderAmount == null && (model.payment_amount_type == null || model.payment_amount_type == "PARTIAL_PAYMENT")) {
                                    openKiosShutDownDialog?.invoke(model)
                                } else if (model.is_kiosk_shutdown == true && model.payment_amount_type == "PARTIAL_PAYMENT") {
                                    openPartialPaymentDialog?.invoke(model)
                                } else {
                                    openOTP?.invoke(model.token)
                                }
                            }

                        }
                    }
                }
        }
    }

    fun getPaymentDetails(func: (PaymentDetailDto?) -> Unit) {
        viewModelScope.launch {
            delay(100)
            kiosRepository.getPaymentDetails(
                stockOpNameId = stockOpNameId,
            )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { state ->
                    state.handleErrorAndLoadingState()
                    if (state is LoadState.Loaded) {
                        func.invoke(state.value)
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

    fun cancelReport(cancelReason: String, note: String, func: () -> Unit) {
        viewModelScope.launch {
            val report = (uiState.value as UiState.Success).report!!
            kiosRepository.cancelReport(
                stockOPNameReportId = report.id,
                cancelReason = cancelReason,
                note = note
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

    val isPayButtonEnabled = combine(
        flow = tncAgree,
        flow2 = mirtaSignature,
        flow3 = statusMitraName
    ) { tncAgree, mirtaSignature, statusMitraName ->
        tncAgree && statusMitraName && mirtaSignature != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    ).asLiveData()

    private fun getErrorMessage(appError: AppError?): String {
        if (appError is AppError.ApiException.BadRequestException) {
            val apiError = appError.toApiError()
            return apiError.message
        }
        return ""
    }

    fun logout(func: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            func()
        }
    }

    companion object {
        private const val NO_BANK_ACCOUNT_ERROR = "NO_BANK_ACCOUNT"
    }
}


