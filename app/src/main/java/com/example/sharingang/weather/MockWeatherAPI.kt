package com.example.sharingang.weather

import com.example.sharingang.models.Weather

/**
 * This mocks Weather API by always returning the same Weather.
 */
class MockWeatherAPI(private val everywhere: Weather) : WeatherAPI {
    override suspend fun getWeather(lat: Double, lon: Double): Weather {
        return everywhere
    }
}
