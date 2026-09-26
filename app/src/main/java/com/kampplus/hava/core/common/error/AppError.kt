package com.kampplus.hava.core.common.error

sealed interface AppError {
    data object Network : AppError

    data class Server(
        val code: Int
    ) : AppError

    data object NotFound : AppError

    data object Parse : AppError

    data class Unknown(
        val cause: Throwable
    ) : AppError
}
