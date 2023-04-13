package com.kitabeli.ae.data

import android.content.Context
import com.kitabeli.ae.data.remote.dto.LoginRequestDto
import com.kitabeli.ae.data.remote.dto.LoginResponseDto
import com.kitabeli.ae.data.remote.service.UserService
import com.kitabeli.ae.model.repository.AuthenticationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val userService: UserService,
) : AuthenticationRepository {
    override fun login(email: String, password: String, role: String): Flow<LoginResponseDto> {
        return userService.login(LoginRequestDto(email, password, role)).map {
            it.payload!!
        }
    }
}