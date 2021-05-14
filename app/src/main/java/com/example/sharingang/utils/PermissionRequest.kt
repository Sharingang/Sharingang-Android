package com.example.sharingang.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource

fun requestPermissionLauncher(
    activity: ComponentActivity,
    callback: () -> Unit
): ActivityResultLauncher<String> {
    return activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            callback()
        }
    }
}

fun doOrGetPermission(
    context: Context,
    activity: Activity,
    permission: String,
    callback: () -> Unit,
    resultLauncher: ActivityResultLauncher<String>?
) {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        callback()
    } else {
        requestPermission(context, activity, permission, resultLauncher!!)
    }
}

fun requestPermission(
    context: Context?,
    fragment: Activity,
    permission: String,
    resultLauncher: ActivityResultLauncher<String>
) {
    if (fragment.shouldShowRequestPermissionRationale(permission)) {
        Toast.makeText(context, "$permission is required", Toast.LENGTH_LONG).show()
    }
    resultLauncher.launch(permission)
}

@SuppressLint("MissingPermission")
fun consumeLocation(
    fusedLocation: FusedLocationProviderClient,
    cancellationTokenSource: CancellationTokenSource,
    callback: (Location) -> Unit
) {
    val getLocationTask =
        fusedLocation.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
    getLocationTask.addOnCompleteListener {
        if (it.isSuccessful && it.result != null) {
            callback(it.result)
        }
    }
}