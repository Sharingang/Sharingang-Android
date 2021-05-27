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
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val args: WeatherFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var binding: FragmentWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)

        viewModel.fetchWeather(args.lat.toDouble(), args.lon.toDouble())

        viewModel.weather.observe(viewLifecycleOwner) {
            if (it != null) {
                onWeatherChange(it)
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun onWeatherChange(it: Weather) {
        binding.weatherLayout.visibility = View.VISIBLE
        binding.weatherCity.text = it.cityName
        binding.weatherDescription.text = it.description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        binding.weatherTemp.text = "%.1f°".format(it.temperature)
        binding.weatherCondition.text = it.condition.toString()
        binding.weatherImage.setImageResource(it.condition.resource())
    }
}