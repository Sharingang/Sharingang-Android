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

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.buttonGetLocalisation.setOnClickListener{
            checkPermissionLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun checkPermissionLocation(){
        Log.e("No error", "Check permission")
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Permission is granted, get location
                Log.e("Hello", "Permission obtained")
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
            )
            currentLocationTask.addOnCompleteListener{task:Task<Location> ->
                if(task.isSuccessful && task.result!=null){
                    lastLocation = task.result
                    binding.localisationDisplay.text =  String.format("Your location is: %s %s", lastLocation!!.longitude, lastLocation!!.latitude)
                } else {
                    Log.e("Error", "The location couldn't be fetched")
                }
            /*
                val result = if(task.isSuccessful && task.result!=null){
                    lastLocation = task.result
                    binding.localisationDisplay.text =  String.format("Your location is: %s %s", lastLocation!!.longitude, lastLocation!!.latitude)
                } else {
                    // TODO ignore exception for now
                    // val exception = task.exception
                }
                */
                // maybe do something with the result
            }
            /*
            fusedLocationClient.lastLocation.addOnSuccessListener { location:Location?->
                // location may be null if the location is off on the device settings, the device never recorder the location
                // or if the google play services on the device has restarted
                lastLocation = location
                binding.localisationDisplay.text = String.format("Your location is: %s %s", location!!.longitude, location.latitude)}
            */
        } else {
            Log.e("Permission not granted", "Error")
            // location permission has not been granted
            // shouldShowRequestPermissionRationale returns false if the user has chosen "not ask again" or if the permission is disabled
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"Location permission is needed to display the location.",Toast.LENGTH_SHORT).show()
            }
            // request location permission
            requestPermissions(Array(1){Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION)
        }
    }
/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==REQUEST_LOCATION){
            // check whether the only required permission has been granted
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                // Location permission has been granted, location can be displayed
                // displayLocation();
            } else {
                // Location permission was denied, we cannot use this feature
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
*/
}