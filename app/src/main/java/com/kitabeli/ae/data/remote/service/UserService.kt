package com.kitabeli.ae.data.remote.service


import com.kitabeli.ae.data.remote.dto.BaseResponseDto
import com.kitabeli.ae.data.remote.dto.LoginRequestDto
import com.kitabeli.ae.data.remote.dto.LoginResponseDto
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("api/v1/ae/login")
    fun login(@Body requestDto: LoginRequestDto): Flow<BaseResponseDto<LoginResponseDto>>
}