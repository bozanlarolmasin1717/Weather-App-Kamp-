package com.kampplus.hava.core.common.error

import javax.inject.Inject

fun interface ErrorMapper {
    fun map(throwable: Throwable): AppError
}

class DefaultErrorMapper @Inject constructor() : ErrorMapper {
    override fun map(throwable: Throwable): AppError = when (throwable) {
        is NoSuchElementException -> AppError.NotFound
        else -> AppError.Unknown(throwable)
    }
}
