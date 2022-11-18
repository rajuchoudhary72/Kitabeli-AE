package com.kitabeli.ae.di.modules

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kitabeli.ae.BuildConfig
import com.kitabeli.ae.data.remote.AuthTokenInterceptor
import com.kitabeli.ae.data.remote.NetworkConnectionInterceptor
import com.kitabeli.ae.data.remote.service.KiosService
import com.kitabeli.ae.data.remote.service.UserService
import com.kitabeli.ae.utils.retrofit.FlowCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }


    @Singleton
    @Provides
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        apiExceptionInterceptor: NetworkConnectionInterceptor,
        tokenAuthenticator: AuthTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(apiExceptionInterceptor)
            .addInterceptor(tokenAuthenticator)
            .build()
    }


    @Singleton
    @Provides

    fun provideRetrofitClient(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_PATH)
            .client(okHttpClient)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .addConverterFactory(
                json.asConverterFactory(contentType)
            )
            .build()
    }


    @Singleton
    @Provides
    fun provideUserService(
        retrofit: Retrofit,
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun provideKiosService(
        retrofit: Retrofit,
    ): KiosService {
        return retrofit.create(KiosService::class.java)
    }

}
