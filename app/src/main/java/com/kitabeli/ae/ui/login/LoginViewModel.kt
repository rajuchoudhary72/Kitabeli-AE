package com.kitabeli.ae.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.R
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.AuthenticationRepository
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.LiveDataValidator
import com.kitabeli.ae.utils.ext.LiveDataValidatorResolver
import com.kitabeli.ae.utils.ext.requireValue
import com.kitabeli.ae.utils.ext.toLoadingState
import com.kitabeli.ae.utils.isValidEmailAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authenticationRepository: AuthenticationRepository,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _isUserSessionAvailable = sessionManager.isUserSessionAvailable()
    val isUserSessionAvailable = _isUserSessionAvailable

    private val _kioskCode = sessionManager.getKioskCode()
    val kioskCode = _kioskCode

    val emailAddress = MutableLiveData<String>()
    val emailAddressValidator = LiveDataValidator(emailAddress).apply {
        addRule(R.string.email_validation_message) { it.isValidEmailAddress().not() }
    }


    val password = MutableLiveData<String>()
    val passwordValidator = LiveDataValidator(password).apply {
        addRule(R.string.password_validation_message) { it.isNullOrBlank() }
    }

    val isAERoleSelected = MutableStateFlow(false)
    val isKORoleSelected = MutableStateFlow(false)
    val role = MutableLiveData<String>()
    val roleValidator = LiveDataValidator(role).apply {
        addRule(R.string.role_validation_message) { it.isNullOrBlank() }
    }

    val isLoginFormValid = MediatorLiveData<Boolean>()

    init {
        isLoginFormValid.value = false
        isLoginFormValid.addSource(emailAddress) { validateForm() }
        isLoginFormValid.addSource(password) { validateForm() }
        isLoginFormValid.addSource(role) { validateForm() }
    }


    private fun validateForm() {
        val validators = listOf(emailAddressValidator, passwordValidator, roleValidator)
        val validatorResolver = LiveDataValidatorResolver(validators)
        isLoginFormValid.value = validatorResolver.isValid()
    }

    fun login() {
        viewModelScope.launch {
            authenticationRepository
                .login(
                    email = emailAddress.requireValue(),
                    password = password.requireValue(),
                    role = role.requireValue()
                )
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let { loginDto ->
                        sessionManager.createSession(loginDto)
                    }
                }
        }
    }

    fun initializeStock(kioskCode: String, onSuccess: (Int?) -> Unit) {
        viewModelScope.launch {
            kiosRepository
                .initializeStock(kioskCode)
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    when (response) {
                        LoadState.Loading -> showLoading(true)
                        is LoadState.Error -> {
                            showLoading(false)
                            onSuccess(null)
                        }

                        is LoadState.Loaded -> {
                            showLoading(false)
                            onSuccess(response.getValueOrNull()?.stockOpnameId)
                        }
                    }
                }
        }
    }

    fun fetchKioskData(onSuccess: (Int?, String?) -> Unit) {
        viewModelScope.launch {
            kiosRepository
                .getKiosData()
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    when (response) {
                        LoadState.Loading -> showLoading(true)
                        is LoadState.Error -> {
                            showLoading(false)
                            onSuccess(null, null)
                        }

                        is LoadState.Loaded -> {
                            showLoading(false)
                            val kioskItem = response.getValueOrNull()?.items?.lastOrNull()
                            onSuccess(kioskItem?.stockOpnameId, kioskItem?.status)
                        }
                    }
                }
        }
    }

    fun onRoleCardClicked(isAE: Boolean) {
        isAERoleSelected.value = isAE
        isKORoleSelected.value = !isAE
        role.value = if (isAE) AE_USER_ROLE else KIOSK_OWNER_ROLE
    }

    companion object {
        const val AE_USER_ROLE = "AE_USER"
        const val KIOSK_OWNER_ROLE = "KIOSK_OWNER"
    }
}