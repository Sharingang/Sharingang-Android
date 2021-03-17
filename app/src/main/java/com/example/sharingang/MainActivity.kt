package com.example.sharingang

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    var permissionGranted: Boolean = false

    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            permissionGranted = if (isGranted) {
                true
            } else {
                Toast.makeText(
                    this,
                    "Storage permission successfully denied. Feature is disabled.",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
        }

    fun checkAndRequestPermission(permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted = true
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    this,
                    rationale,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}