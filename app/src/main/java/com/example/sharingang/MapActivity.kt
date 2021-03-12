package com.example.sharingang

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

const val REQUEST_LOCATION: Int = 0

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Allows the cancellation of a location request if, for example, the user exists the activity
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.buttonGetLocation.setOnClickListener {
            checkLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                binding.locationDisplay.text = String.format("Your location is: %s %s", lastLocation!!.longitude, lastLocation!!.latitude)
            }
        }
    }

    private fun checkLocationPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updateLocation()
        } else {
            Log.e("Error", "Permission not granted")
            // shouldShowRequestPermissionRationale returns false if the user has chosen "not ask again" or if the permission is disabled
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location permission is needed to display the location.", Toast.LENGTH_LONG).show()
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }
}