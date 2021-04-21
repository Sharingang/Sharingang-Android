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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentMapBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.utils.doOrGetPermission
import com.example.sharingang.utils.requestPermissionLauncher
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

const val DEFAULT_ZOOM = 15

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher = requestPermissionLauncher(this) {
        doOrGetPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION, { startLocationUpdates() }, null
        )
    }
    private var lastLocation: Location? = null
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            lastLocation = locationResult.lastLocation
            if (!hasCameraMovedOnce) {
                moveCameraToLastLocation()
                hasCameraMovedOnce = true
            }
        }
    }
    private var lastLocationMarker: Marker? = null
    private var map: GoogleMap? = null

    private val viewModel: ItemsViewModel by activityViewModels()
    private var hasCameraMovedOnce by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        hasCameraMovedOnce = false
        viewModel.searchResults.observe(viewLifecycleOwner, {
            addItemMarkers(it)
        })
        binding.mapGetMyLocation.setOnClickListener {
            if (lastLocation != null) {
                moveCameraToLastLocation()
            }
        }
        binding.mapStartSearch.setOnClickListener {
            this.findNavController()
                .navigate(MapFragmentDirections.actionMapFragmentToSearchFragment())
        }
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    private fun addItemMarkers(items: List<Item>) {
        map?.clear()
        for (item: Item in items) {
            val addedMarker = map?.addMarker(
                MarkerOptions().position(LatLng(item.latitude, item.longitude))
                    .title(item.title)
                    .snippet(item.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            addedMarker?.showInfoWindow()
            addedMarker?.tag = item
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Show blue dot at current location
        map?.isMyLocationEnabled = true
        // We already have a custom button so we remove the default one
        map?.uiSettings?.isMyLocationButtonEnabled = false

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


    private fun moveCameraToLastLocation() {
        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lastLocation!!.latitude,
                    lastLocation!!.longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.setOnMarkerClickListener { marker: Marker ->
            map?.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            if (marker != lastLocationMarker) {
                val markerItem = marker.tag as Item
                this.findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToDetailedItemFragment(markerItem)
                )
            }
            true
        }
        addItemMarkers(viewModel.searchResults.value ?: listOf())

        doOrGetPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            { startLocationUpdates() },
            requestPermissionLauncher
        )
    }

    override fun onResume() {
        binding.mapView.onResume()
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
