package com.example.sharingang.ui.fragments

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

class WeatherFragment : Fragment() {
    private val args: WeatherFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentWeatherBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)

        viewModel.fetchWeather(args.lat.toDouble(), args.lon.toDouble())

        viewModel.weather.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.weatherText.text = it.toString()
            }
        }
        return binding.root
    }
}