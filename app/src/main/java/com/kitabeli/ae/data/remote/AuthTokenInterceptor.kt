package com.kitabeli.ae.data.remote

import android.content.Context
import android.util.Log
import com.kitabeli.ae.BuildConfig
import com.kitabeli.ae.data.local.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenInterceptor @Inject constructor(
    @ApplicationContext val context: Context,
    private val sessionManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("accept", "application/json")


        if (chain.request().url.host.contains("amazonaws").not())
            runBlocking {
                sessionManager.fetchAuthToken()
            }?.let { authToken ->
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Token: $authToken")
                requestBuilder.addHeader("Authorization", "Bearer $authToken")
            }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val TAG = "AuthTokenInterceptor"
    }
}
