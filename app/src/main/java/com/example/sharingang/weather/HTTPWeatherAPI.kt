package com.example.sharingang.weather

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sharingang.models.Weather
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HTTPWeatherAPI(private val context: Context) : WeatherAPI {
    override suspend fun getWeather(lat: Double, lon: Double): Weather? = suspendCoroutine { cont ->
        val queue = Volley.newRequestQueue(context)
        var apiKey: String? = null
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {
                apiKey = metaData.getString("WEATHER_API_KEY")
            }
        if (apiKey == null) {
            Log.d("Weather API", "No Key :(")
            cont.resume(null)
        }
        val url =
            "http://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric"
        val req = JsonObjectRequest(Request.Method.GET, url, null,
            {
                val firstWeather = it.getJSONArray("weather").getJSONObject(0)
                val main = it.getJSONObject("main")
                cont.resume(
                    Weather(
                        Weather.Condition.fromString(firstWeather.getString("main")),
                        firstWeather.getString("description"),
                        main.getDouble("temp"),
                        it.getString("name")
                    )
                )
            },
            {
                Log.d("Weather API Error", it.toString())
                cont.resume(null)
            }
        )
        queue.add(req)
    }
}