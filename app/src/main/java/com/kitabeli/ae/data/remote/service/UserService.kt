package com.kitabeli.ae.data.remote.service


import kotlinx.coroutines.flow.Flow
import retrofit2.http.POST

interface UserService {

    @POST("api/v1/ae/login")
    fun login(): Flow<Any>


}