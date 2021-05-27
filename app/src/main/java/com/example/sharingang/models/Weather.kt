package com.example.sharingang.models

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
    }

}
