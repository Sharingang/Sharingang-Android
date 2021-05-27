package com.example.sharingang.models

import com.example.sharingang.R

/**
 * Represents information about whether at some place.
 */
data class Weather(
    /**
     * The type of weather going on.
     */
    val condition: Condition,
    /**
     * A more precise description of the Meteorological conditions
     */
    val description: String,
    /**
     * The temperature, in celsius
     */
    val temperature: Double,
    /**
     * The name of the city where this weather is happening
     */
    val cityName: String
) {
    /**
     * Represents a type of weather condition.
     */
    enum class Condition {
        Thunderstorm,
        Drizzle,
        Rain,
        Snow,
        Mist,
        Smoke,
        Haze,
        Dust,
        Fog,
        Ash,
        Squall,
        Tornado,
        Clear,
        Clouds,
        Other;

        companion object {
            fun fromString(condition: String): Condition = when (condition) {
                "Thunderstorm" -> Thunderstorm
                "Drizzle" -> Drizzle
                "Rain" -> Rain
                "Snow" -> Snow
                "Mist" -> Mist
                "Smoke" -> Smoke
                "Haze" -> Haze
                "Dust" -> Dust
                "Fog" -> Fog
                "Ash" -> Ash
                "Squall" -> Squall
                "Tornado" -> Tornado
                "Clear" -> Clear
                "Clouds" -> Clouds
                else -> Other
            }
        }

        fun resource(): Int = when (this) {
            Thunderstorm -> R.mipmap.thunderstorm
            Drizzle -> R.mipmap.drizzle
            Rain -> R.mipmap.rain
            Snow -> R.mipmap.snow
            Mist -> R.mipmap.mist
            Smoke -> R.mipmap.smoke
            Haze -> R.mipmap.haze
            Dust -> R.mipmap.dust
            Fog -> R.mipmap.mist
            Ash -> R.mipmap.ash
            Squall -> R.mipmap.squall
            Tornado -> R.mipmap.tornado
            Clear -> R.mipmap.clear_skies
            Clouds -> R.mipmap.clouds
            else -> R.mipmap.dunno
        }
    }

}
