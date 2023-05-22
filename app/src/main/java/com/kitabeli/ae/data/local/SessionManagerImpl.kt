package com.kitabeli.ae.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "com.kitabeli.ae.data.local.session"
    )

    override suspend fun createSession(loginResponseDto: LoginResponseDto) {
        val hasKioskCode = loginResponseDto.kioskCode != null
        val hasKioskRole = loginResponseDto.role == KIOSK_OWNER_ROLE
        context.dataStore.edit { userSession ->
            userSession[AUTH_TOKEN] = loginResponseDto.jwtToken
            userSession[EMAIL] = loginResponseDto.email
            userSession[PHONE] = loginResponseDto.email
            userSession[AE_ID] = loginResponseDto.aeId ?: 0
            userSession[IS_KIOSK_OWNER] = hasKioskCode && hasKioskRole
            userSession[KIOSK_CODE] = loginResponseDto.kioskCode.orEmpty()
            userSession[USER_ROLE] = loginResponseDto.role
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
            userSession[AE_ID] ?: 0
        }
    }

    override fun getUserEmail(): Flow<String> {
        return context.dataStore.data.map { userSession ->
            userSession[EMAIL].orEmpty()
        }
    }

    override fun isKioskOwner(): Flow<Boolean> {
        return context.dataStore.data.map { userSession ->
            userSession[IS_KIOSK_OWNER] ?: false
        }
    }

    override fun getKioskCode(): Flow<String> {
        return context.dataStore.data.map { userSession ->
            userSession[KIOSK_CODE].orEmpty()
        }
    }

    override fun getUserRole(): Flow<String> {
        return context.dataStore.data.map { userSession ->
            userSession[USER_ROLE] ?: "AE_USER"
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
        val IS_KIOSK_OWNER = booleanPreferencesKey("is_kiosk_owner")
        val KIOSK_CODE = stringPreferencesKey("kiosk_code")
        val USER_ROLE = stringPreferencesKey("user_role")
        const val KIOSK_OWNER_ROLE = "KIOSK_OWNER"
    }
}