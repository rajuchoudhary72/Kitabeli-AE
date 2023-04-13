package com.kitabeli.ae.model.repository

import com.kitabeli.ae.data.remote.dto.LoginResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun login(email: String, password: String, role: String): Flow<LoginResponseDto>
}