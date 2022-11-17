package com.kitabeli.ae.utils.ext

import androidx.annotation.StringRes
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.exception.BadRequestException
import com.kitabeli.ae.data.remote.exception.NetworkNotConnectedException
import com.kitabeli.ae.model.ApiError
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.ErrorType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException

/**
 * Convert Throwable to AppError
 */
fun Throwable?.toAppError(): AppError? {
    this?.printStackTrace()
    return when (this) {
        null -> null
        is AppError -> this
        is NetworkNotConnectedException -> AppError.ApiException.NetworkNotConnectedException(this)
        is HttpException -> this.httpExceptionToAppError()
        is BadRequestException -> AppError.ApiException.BadRequestException(this)
        else -> AppError.UnknownException(this)
    }
}

private fun HttpException.httpExceptionToAppError(): AppError {
    val errorResponse = response()
    return if (errorResponse != null) {
        val error = errorResponse.errorBody()?.string()
        if (error != null && error.isEmpty().not()) {
            try {
                AppError.ApiException.BadRequestException(
                    BadRequestException(
                        Json.decodeFromString(
                            error
                        )
                    )
                )
            } catch (e: Exception) {
                AppError.UnknownException(e)
            }
        } else {
            when (ErrorType.getErrorType(response()!!.code())) {
                ErrorType.UNAUTHORIZED ->
                    AppError.ApiException.SessionNotFoundException(this)

                ErrorType.SYSTEM_ERROR ->
                    AppError.ApiException.ServerException(this)

                else ->
                    AppError.ApiException.NetworkException(this)
            }
        }
    } else {
        AppError.ApiException.UnknownException(this)
    }
}


/**
 * Convert AppError to String Resources
 */
@StringRes
fun AppError.stringRes() = when (this) {
    is AppError.ApiException.NetworkException -> R.string.error_network
    is AppError.ApiException.NetworkNotConnectedException -> R.string.error_no_internet_connection
    is AppError.ApiException.ServerException -> R.string.error_server
    is AppError.ApiException.SessionNotFoundException -> R.string.error_unknown
    is AppError.ApiException.UnknownException -> R.string.error_unknown
    is AppError.UnknownException -> R.string.error_unknown
    else -> R.string.error_unknown
}

fun AppError.toApiError(): ApiError {
    if (this is AppError.ApiException.BadRequestException) {
        return (this.cause as BadRequestException).apiError
    } else {
        throw IllegalArgumentException("AppError is not a type of BadRequestException")
    }
}

