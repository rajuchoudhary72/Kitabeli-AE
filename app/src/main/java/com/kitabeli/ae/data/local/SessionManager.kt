package com.kitabeli.ae.data.local


interface SessionManager {
    suspend fun fetchAuthToken(): String?
    suspend fun isUserSessionAvailable(): Boolean
}