package com.kitabeli.ae.data.remote.exception

import com.kitabeli.ae.model.ApiError

class BadRequestException(val apiError: ApiError) : Throwable(apiError.message)
