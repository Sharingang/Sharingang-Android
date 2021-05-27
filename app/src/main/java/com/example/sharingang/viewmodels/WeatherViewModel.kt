package com.example.sharingang.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingang.models.Weather
import com.example.sharingang.weather.WeatherAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val api: WeatherAPI
) : ViewModel() {
    private val _weather: MutableLiveData<Weather?> = MutableLiveData(null)
    val weather: LiveData<Weather?>
        get() = _weather

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weather = api.getWeather(lat, lon)
            _weather.value = weather
        }
    }
}