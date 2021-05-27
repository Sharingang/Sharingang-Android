package com.example.sharingang.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.sharingang.R
import com.example.sharingang.databinding.FragmentWeatherBinding
import com.example.sharingang.models.Weather
import com.example.sharingang.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val args: WeatherFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentWeatherBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)

        viewModel.fetchWeather(args.lat.toDouble(), args.lon.toDouble())

        viewModel.weather.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.weatherLayout.visibility = View.VISIBLE
                binding.weatherCity.text = it.cityName
                binding.weatherDescription.text = it.description
                binding.weatherTemp.text = "%.1f°".format(it.temperature)
                binding.weatherCondition.text = it.condition.toString()
                val resource = when (it.condition) {
                    Weather.Condition.Thunderstorm -> R.mipmap.thunderstorm
                    Weather.Condition.Drizzle -> R.mipmap.drizzle
                    Weather.Condition.Rain -> R.mipmap.rain
                    Weather.Condition.Snow -> R.mipmap.snow
                    Weather.Condition.Mist -> R.mipmap.mist
                    Weather.Condition.Smoke -> R.mipmap.smoke
                    Weather.Condition.Haze -> R.mipmap.haze
                    Weather.Condition.Dust -> R.mipmap.dust
                    Weather.Condition.Fog -> R.mipmap.mist
                    Weather.Condition.Ash -> R.mipmap.ash
                    Weather.Condition.Squall -> R.mipmap.squall
                    Weather.Condition.Tornado -> R.mipmap.tornado
                    Weather.Condition.Clear -> R.mipmap.clear_skies
                    Weather.Condition.Clouds -> R.mipmap.clouds
                    else -> R.mipmap.dunno
                }
                binding.weatherImage.setImageResource(resource)
            }
        }
        return binding.root
    }
}