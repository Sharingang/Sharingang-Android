package com.example.sharingang

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

const val REQUEST_LOCATION:Int = 0
class MapActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMapBinding
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Allows the cancellation of a location request if, for example, the user exists the activity
    private var cancellationTokenSource = CancellationTokenSource()

    /* Might be useful later
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 2500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.buttonGetLocalisation.setOnClickListener{
            checkPermissionLocation()
        }
    }

    private fun checkPermissionLocation(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Permission is granted, get location
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
            )
            currentLocationTask.addOnCompleteListener{task:Task<Location> ->
                if(task.isSuccessful && task.result!=null){
                    lastLocation = task.result
                    binding.localisationDisplay.text =  String.format("Your location is: %s %s", lastLocation!!.longitude, lastLocation!!.latitude)
                } else {
                    // TODO add something in case the location cannot be fetched for some reason
                }
                // maybe do something with the result
            }
        } else {
            Log.e("Error", "Permission not granted")
            // location permission has not been granted
            // shouldShowRequestPermissionRationale returns false if the user has chosen "not ask again" or if the permission is disabled
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"Location permission is needed to display the location.",Toast.LENGTH_LONG).show()
            }
            // request location permission
            requestPermissions(Array(1){Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION)
        }
    }
}