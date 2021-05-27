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
            }
        }
        return binding.root
    }
}