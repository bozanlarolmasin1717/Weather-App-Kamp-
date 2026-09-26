package com.kampplus.hava.core.network.error

import com.kampplus.hava.core.common.error.AppError
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class NetworkErrorMapperTest {

    private val mapper = NetworkErrorMapper()

    @Test
    fun `no internet and timeouts map to network error`() {
        assertEquals(AppError.Network, mapper.map(UnknownHostException()))
        assertEquals(AppError.Network, mapper.map(SocketTimeoutException()))
    }

    @Test
    fun `http 404 maps to not found, other codes to server error`() {
        assertEquals(AppError.NotFound, mapper.map(httpException(404)))
        assertEquals(AppError.Server(400), mapper.map(httpException(400)))
        assertEquals(AppError.Server(503), mapper.map(httpException(503)))
    }

    @Test
    fun `malformed json maps to parse error`() {
        assertEquals(AppError.Parse, mapper.map(SerializationException("bad json")))
    }

    @Test
    fun `anything else is unknown`() {
        assertTrue(mapper.map(IllegalStateException()) is AppError.Unknown)
    }

    private fun httpException(code: Int) = HttpException(Response.error<Unit>(code, "".toResponseBody()))
}
