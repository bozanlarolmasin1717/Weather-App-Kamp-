package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.testing.FakeCityRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.city
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchCityWeathersUseCaseTest {

    private val cityRepository = FakeCityRepository()
    private var weatherCalls = 0
    private val weatherRepository = FakeWeatherRepository().apply {
        val default = currentWeatherResult
        currentWeatherResult = { cities ->
            weatherCalls++
            default(cities)
        }
    }
    private val useCase = SearchCityWeathersUseCase(cityRepository, weatherRepository)

    @Test
    fun `found cities are enriched with current weather`() = runTest {
        cityRepository.searchResult = { AppResult.Success(listOf(city(id = 1, name = "Berlin"), city(id = 2, name = "Bern"))) }

        val result = useCase("  Ber ").first() as AppResult.Success

        assertEquals(listOf("Berlin", "Bern"), result.data.map { it.city.name })
        assertEquals(listOf("Ber"), cityRepository.queries)
    }

    @Test
    fun `no match returns empty list without calling weather api`() = runTest {
        cityRepository.searchResult = { AppResult.Success(emptyList()) }

        val result = useCase("xqzw").first() as AppResult.Success

        assertTrue(result.data.isEmpty())
        assertEquals(0, weatherCalls)
    }

    @Test
    fun `search failure is propagated`() = runTest {
        cityRepository.searchResult = { AppResult.Failure(AppError.Network) }

        assertEquals(AppResult.Failure(AppError.Network), useCase("Ankara").first())
    }
}
