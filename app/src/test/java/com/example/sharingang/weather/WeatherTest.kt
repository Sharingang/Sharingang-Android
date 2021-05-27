package com.example.sharingang.weather

import com.example.sharingang.R
import com.example.sharingang.models.Weather
import org.junit.Test

class WeatherTest {

    @Test
    fun testWeatherConditionParsing() {
        assert(Weather.Condition.fromString("Thunderstorm") == Weather.Condition.Thunderstorm)
        assert(Weather.Condition.fromString("Drizzle") == Weather.Condition.Drizzle)
        assert(Weather.Condition.fromString("Rain") == Weather.Condition.Rain)
        assert(Weather.Condition.fromString("Snow") == Weather.Condition.Snow)
        assert(Weather.Condition.fromString("Mist") == Weather.Condition.Mist)
        assert(Weather.Condition.fromString("Smoke") == Weather.Condition.Smoke)
        assert(Weather.Condition.fromString("Haze") == Weather.Condition.Haze)
        assert(Weather.Condition.fromString("Dust") == Weather.Condition.Dust)
        assert(Weather.Condition.fromString("Fog") == Weather.Condition.Fog)
        assert(Weather.Condition.fromString("Ash") == Weather.Condition.Ash)
        assert(Weather.Condition.fromString("Squall") == Weather.Condition.Squall)
        assert(Weather.Condition.fromString("Tornado") == Weather.Condition.Tornado)
        assert(Weather.Condition.fromString("Clear") == Weather.Condition.Clear)
        assert(Weather.Condition.fromString("Clouds") == Weather.Condition.Clouds)
        assert(Weather.Condition.fromString("Other") == Weather.Condition.Other)
    }

    @Test
    fun testWeatherConditionResources() {
        assert(Weather.Condition.Thunderstorm.resource() == R.mipmap.thunderstorm)
        assert(Weather.Condition.Drizzle.resource() == R.mipmap.drizzle)
        assert(Weather.Condition.Rain.resource() == R.mipmap.rain)
        assert(Weather.Condition.Snow.resource() == R.mipmap.snow)
        assert(Weather.Condition.Mist.resource() == R.mipmap.mist)
        assert(Weather.Condition.Smoke.resource() == R.mipmap.smoke)
        assert(Weather.Condition.Haze.resource() == R.mipmap.haze)
        assert(Weather.Condition.Dust.resource() == R.mipmap.dust)
        assert(Weather.Condition.Fog.resource() == R.mipmap.mist)
        assert(Weather.Condition.Ash.resource() == R.mipmap.ash)
        assert(Weather.Condition.Squall.resource() == R.mipmap.squall)
        assert(Weather.Condition.Tornado.resource() == R.mipmap.tornado)
        assert(Weather.Condition.Clear.resource() == R.mipmap.clear_skies)
        assert(Weather.Condition.Clouds.resource() == R.mipmap.clouds)
        assert(Weather.Condition.Other.resource() == R.mipmap.dunno)
    }
}