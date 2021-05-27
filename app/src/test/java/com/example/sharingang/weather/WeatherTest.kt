package com.example.sharingang.weather

import com.example.sharingang.models.Weather
import org.junit.Test

class WeatherTest {

    @Test
    fun testWeatherConditionParsing() {
        val values = enumValues<Weather.Condition>()
        for (condition in values) {
            assert(Weather.Condition.fromString(condition.toString()) == condition)
        }
    }
}