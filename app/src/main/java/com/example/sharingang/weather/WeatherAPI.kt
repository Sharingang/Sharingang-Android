package com.example.sharingang.weather

import com.example.sharingang.models.Weather

/**
 * WeatherAPI abstracts over an API used to fetch weather information.
 *
 * This allows us to query whether information at a certain place on earth.
 */
interface WeatherAPI {
    /**
     * Get the weather at a certain place
     *
     * @param lat the latitude of some spot on earth
     * @param lon the longitude of some spot on earth
     * @return the current weather, or null if no weather information could be fetched
     */
    suspend fun getWeather(lat: Double, lon: Double): Weather?
}