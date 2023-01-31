package com.kitabeli.ae.ui.post_login

import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.model.repository.AuthenticationRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostLoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

}