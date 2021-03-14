package com.example.sharingang

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

const val REQUEST_LOCATION: Int = 0
const val DEFAULT_ZOOM = 15.0

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGranted: Boolean = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                permissionGranted = true
                startLocationUpdates()
            } else {
                Toast.makeText(
                    this,
                    "Location permission successfully denied. Feature is disabled.",
                    Toast.LENGTH_LONG
                ).show()
                permissionGranted = false
            }
        }

    private var lastLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private var lastLocationMarker: Marker? = null
    private var map: GoogleMap? = null

    // Allows the cancellation of a location request if, for example, the user exists the activity
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lastLocation = locationResult.lastLocation
                updateLocationText()
                moveCameraToLastLocation()
                moveLastLocationMarker()
            }
        }
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!permissionGranted) {
            requestLocationPermission()
        } else {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2500
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                },
                locationCallback,
                Looper.getMainLooper()
            )

        }
    }

    private fun updateLocationText() {
        binding.locationDisplay.text = String.format(
            "Your location is: %s %s",
            lastLocation!!.longitude,
            lastLocation!!.latitude
        )
    }

    private fun moveCameraToLastLocation() {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lastLocation!!.latitude,
                    lastLocation!!.longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
    }

    private fun moveLastLocationMarker() {
        lastLocationMarker?.remove()
        lastLocationMarker = map?.addMarker(
            MarkerOptions().position(
                LatLng(
                    lastLocation!!.latitude,
                    lastLocation!!.longitude
                )
            )
        )
    }

    private fun requestLocationPermission() {
        // shouldShowRequestPermissionRationale returns false if the user has chosen "not ask again" or if the permission is disabled
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(
                this,
                "Location permission is required to display your location on the map.",
                Toast.LENGTH_LONG
            ).show()
        }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
    }

    override fun onResume() {
        binding.mapView.onResume()
        startLocationUpdates()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}