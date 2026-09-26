package com.kampplus.hava.core.network.error

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.error.ErrorMapper
import java.io.IOException
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

/** Ağ katmanının teknik hatalarını uygulamanın hata sözlüğüne çevirir. */
class NetworkErrorMapper @Inject constructor() : ErrorMapper {
    override fun map(throwable: Throwable): AppError = when (throwable) {
        is IOException -> AppError.Network
        is HttpException -> if (throwable.code() == HTTP_NOT_FOUND) AppError.NotFound else AppError.Server(throwable.code())
        is SerializationException -> AppError.Parse
        is NoSuchElementException -> AppError.NotFound
        else -> AppError.Unknown(throwable)
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
