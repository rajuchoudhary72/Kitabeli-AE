package com.kitabeli.ae.utils.retrofit

import com.kitabeli.ae.utils.ext.bodyOrThrow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.awaitResponse
import java.lang.reflect.Type

class FlowCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Flow<T>> {

    override fun responseType() = responseType

    @ExperimentalCoroutinesApi
    override fun adapt(call: Call<T>): Flow<T> = flow {
        val response = call.awaitResponse()
        emit(response.bodyOrThrow())
    }
}
