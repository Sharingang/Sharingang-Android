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

    @Test
    fun testWeatherConditionResources() {
        val values = enumValues<Weather.Condition>()
        for (condition1 in values) {
            if (condition1 == Weather.Condition.Mist) {
                continue
            }
            for (condition2 in values) {
                if (condition2 == Weather.Condition.Mist) {
                    continue
                }
                assert((condition1.resource() == condition2.resource()) == (condition1 == condition2))
            }
        }
    }
}