package com.example.sharingang.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationToken

fun doOrGetLocationPermission(
    context: Context,
    fragment: Fragment,
    callback: () -> Unit,
    resultLauncher: ActivityResultLauncher<String>?
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        callback()
    } else {
        requestPermission(
            context,
            fragment,
            Manifest.permission.ACCESS_FINE_LOCATION,
            resultLauncher!!
        )
    }
}

@SuppressLint("MissingPermission")
fun consumeLocation(
    fusedLocation: FusedLocationProviderClient,
    cancellationToken: CancellationToken,
    callback: (Location) -> Unit
) {
    val getLocationTask =
        fusedLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken)
    getLocationTask.addOnCompleteListener {
        if (it.isSuccessful && it.result != null) {
            callback(it.result)
        }
    }
}