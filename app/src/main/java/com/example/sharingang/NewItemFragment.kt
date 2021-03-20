package com.example.sharingang

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.sharingang.databinding.FragmentNewItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

class NewItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentNewItemBinding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission successfully denied. Feature is disabled.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Allows the cancellation of a location request if, for example, the user exists the activity
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)
        binding.createItemButton.setOnClickListener { view: View ->
            viewModel.addItem(
                Item(
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    description = binding.description ?: "",
                    title = binding.title ?: "",
                    category = binding.categorySpinner.selectedItemPosition,
                    categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
                    latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
                    longitude = binding.longitude?.toDoubleOrNull() ?: 0.0
                )
            )
            view.findNavController().navigate(R.id.action_newItemFragment_to_itemsListFragment)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.newItemGetLocation.setOnClickListener {
            getLocation()
        }
        return binding.root
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val getLocationTask = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            getLocationTask.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    binding.latitude = it.result.latitude.toString()
                    binding.longitude = it.result.longitude.toString()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        // shouldShowRequestPermissionRationale returns false if the user has chosen "not ask again" or if the permission is disabled
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(
                requireContext(),
                "Location permission is required to display your location on the map.",
                Toast.LENGTH_LONG
            ).show()
        }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}