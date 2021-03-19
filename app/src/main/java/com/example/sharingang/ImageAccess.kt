package com.example.sharingang

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


class ImageAccess(private val registry: ActivityResultRegistry, private val callback: (Uri?) -> Unit) : DefaultLifecycleObserver {

    var storagePermissionGranted: Boolean = false
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>

    lateinit var pickImage : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        requestStoragePermissionLauncher =
            registry.register(
                "storagePermission",
                owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    /*Toast.makeText(
                        owner,
                        "Storage permission successfully denied. Feature is disabled.",
                        Toast.LENGTH_LONG
                    ).show()*/
                }
                storagePermissionGranted = isGranted

            }
        pickImage =
            registry.register("openGallery", owner, ActivityResultContracts.GetContent(), callback)
    }

    fun checkAndRequestPermission(permission: String, rationale: String, context: Activity) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> storagePermissionGranted = true
                }
            }
            shouldShowRequestPermissionRationale(context, permission) -> {
                Toast.makeText(
                    context,
                    rationale,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> requestStoragePermissionLauncher.launch(
                        permission
                    )
                }
            }
        }
    }

    fun openGallery() {
        pickImage.launch("image/*")
    }
}