package com.kitabeli.ae.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kitabeli.ae.data.remote.dto.LoginResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SessionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SessionManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.kitabeli.ae.data.local.session")
    override suspend fun createSession(loginResponseDto: LoginResponseDto) {
        context.dataStore.edit { userSession ->
            userSession[AUTH_TOKEN] = loginResponseDto.jwtToken
            userSession[EMAIL] = loginResponseDto.email
            userSession[PHONE] = loginResponseDto.email
            userSession[AE_ID] = loginResponseDto.aeId
        }
    }

    override suspend fun fetchAuthToken(): String? {
        return context.dataStore.data.map { userSession ->
            userSession[AUTH_TOKEN]
        }.first()
    }


    override fun isUserSessionAvailable(): Flow<Boolean> {
        return context.dataStore.data.map { userSession ->
            userSession[AUTH_TOKEN].isNullOrBlank().not()
        }
    }

    override fun getAeId(): Flow<Int> {
        return context.dataStore.data.map { userSession ->
            userSession[AE_ID]!!
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit {
            it.clear()
        }
    }

    companion object {
        val AE_ID = intPreferencesKey("ae_id")
        val FULL_NAME = stringPreferencesKey("full_name")
        val PHONE = stringPreferencesKey("phone")
        val EMAIL = stringPreferencesKey("email")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }
}