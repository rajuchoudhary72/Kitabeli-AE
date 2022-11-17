package com.kitabeli.ae.data

import android.content.Context
import com.kitabeli.ae.data.remote.service.UserService
import com.kitabeli.ae.model.repository.AuthenticationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val userService: UserService,
) : AuthenticationRepository {


}