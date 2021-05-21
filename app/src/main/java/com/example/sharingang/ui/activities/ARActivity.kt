package com.example.sharingang.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.example.sharingang.R
import com.example.sharingang.databinding.ActivityAractivityBinding
import com.example.sharingang.models.Location
import com.example.sharingang.shake.HeadingListener
import com.example.sharingang.viewmodels.ARViewModel
import com.google.android.gms.location.*

/**
 * An Activity we use for the AR interactions.
 *
 * We create a separate activity instead of a fragment, because we need to listen
 * to extra sensors and other things like that, and we want to avoid cluttering up
 * the main activity with all of that.
 */
class ARActivity : AppCompatActivity() {
    // The arguments passed in when navigating to this activity
    // This is how we get information about the item we're looking at
    private val args: ARActivityArgs by navArgs()

    // The view model holding the state of this activity
    private val viewModel: ARViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private lateinit var headingListener: HeadingListener

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startLocationUpdates()
            }
        }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = Location(
                locationResult.lastLocation.latitude,
                locationResult.lastLocation.longitude
            )
            viewModel.setLocation(location)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aractivity)

        val binding: ActivityAractivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_aractivity)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setItemLocation(Location(args.item.latitude, args.item.longitude))

        setupHeadingListener()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setupHeadingListener()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermissions()
    }

    private fun setupHeadingListener() {
        headingListener = HeadingListener {
            viewModel.setHeading(it)
        }
    }

    override fun onResume() {
        super.onResume()
        headingListener.registerWith(sensorManager)
    }

    override fun onPause() {
        sensorManager.unregisterListener(headingListener)
        super.onPause()
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    this,
                    "We need to access your location for AR features",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 2500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            },
            locationCallback,
            mainLooper
        )
    }
}