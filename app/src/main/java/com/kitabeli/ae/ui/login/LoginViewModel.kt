package com.kitabeli.ae.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.R
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.model.repository.AuthenticationRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.LiveDataValidator
import com.kitabeli.ae.utils.ext.LiveDataValidatorResolver
import com.kitabeli.ae.utils.ext.requireValue
import com.kitabeli.ae.utils.ext.toLoadingState
import com.kitabeli.ae.utils.isValidEmailAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val _isUserSessionAvailable = sessionManager.isUserSessionAvailable()
    val isUserSessionAvailable = _isUserSessionAvailable

    val emailAddress = MutableLiveData<String>()
    val emailAddressValidator = LiveDataValidator(emailAddress).apply {
        addRule(R.string.email_validation_message) { it.isValidEmailAddress().not() }
    }


    val password = MutableLiveData<String>()
    val passwordValidator = LiveDataValidator(password).apply {
        addRule(R.string.password_validation_message) { it.isNullOrBlank() }
    }

    val isLoginFormValid = MediatorLiveData<Boolean>()


    init {
        isLoginFormValid.value = false
        isLoginFormValid.addSource(emailAddress) { validateForm() }
        isLoginFormValid.addSource(password) { validateForm() }
    }


    private fun validateForm() {
        val validators = listOf(emailAddressValidator, passwordValidator)
        val validatorResolver = LiveDataValidatorResolver(validators)
        isLoginFormValid.value = validatorResolver.isValid()
    }

    fun login() {
        viewModelScope.launch {
            authenticationRepository
                .login(
                    email = emailAddress.requireValue(),
                    password = password.requireValue()
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
}