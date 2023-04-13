package com.kitabeli.ae.data.local

import com.kitabeli.ae.data.remote.dto.LoginResponseDto
import kotlinx.coroutines.flow.Flow


interface SessionManager {
    suspend fun createSession(loginResponseDto: LoginResponseDto)
    suspend fun fetchAuthToken(): String?
    fun isUserSessionAvailable(): Flow<Boolean>
    fun getAeId(): Flow<Int>
    fun getUserEmail(): Flow<String>
    fun isKioskOwner(): Flow<Boolean>
    fun getKioskCode(): Flow<String>
    fun getUserRole(): Flow<String>
    suspend fun clearSession()
}