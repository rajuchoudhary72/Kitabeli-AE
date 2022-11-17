package com.kitabeli.ae.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SessionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SessionManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override suspend fun fetchAuthToken(): String? {
        return ""
    }


    override suspend fun isUserSessionAvailable(): Boolean {
        return false
    }


    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val FULL_NAME = stringPreferencesKey("full_name")
        val PHONE = stringPreferencesKey("phone")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }
}