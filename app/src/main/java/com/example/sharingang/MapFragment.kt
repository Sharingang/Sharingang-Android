package com.example.sharingang

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sharingang.databinding.FragmentMapBinding
import com.example.sharingang.utils.doOrGetPermission
import com.example.sharingang.utils.requestPermissionLauncher
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

const val DEFAULT_ZOOM = 15

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher = requestPermissionLauncher(this) {
        doOrGetPermission(
            this.context,
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            { startLocationUpdates() },
            null
        )
    }
    private var lastLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private var lastLocationMarker: Marker? = null
    private var map: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
        return binding.root
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
            Looper.getMainLooper()
        )
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
    }

    override fun onResume() {
        binding.mapView.onResume()
        doOrGetPermission(
            this.context, this, android.Manifest.permission.ACCESS_FINE_LOCATION,
            { startLocationUpdates() }, requestPermissionLauncher
        )
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