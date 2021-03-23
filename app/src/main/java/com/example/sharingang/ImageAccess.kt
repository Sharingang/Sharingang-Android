package com.example.sharingang

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


class ImageAccess(private val registry: FragmentActivity, private val callback: (Uri?) -> Unit) :
    DefaultLifecycleObserver {

    private var storagePermissionGranted: Boolean = false
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>

    private lateinit var pickImage: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        requestStoragePermissionLauncher =
            registry.activityResultRegistry.register(
                "storagePermission",
                owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted)
                    Toast.makeText(registry, "Storage permission successfully denied. Feature is disabled.", Toast.LENGTH_LONG).show()
                storagePermissionGranted = isGranted
            }
        pickImage =
            registry.activityResultRegistry.register(
                "openGallery",
                owner,
                ActivityResultContracts.GetContent(),
                callback
            )
    }

    fun unregister() {
        pickImage.unregister()
        requestStoragePermissionLauncher.unregister()
    }

    private fun checkAndRequestPermission(
        permission: String,
        rationale: String,
        context: Activity
    ) {
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
                Toast.makeText(context, rationale, Toast.LENGTH_LONG).show()
            }
            else -> {
                when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> requestStoragePermissionLauncher.launch(permission)
                }
            }
        }
    }

    fun openGallery(activity: Activity) {
        checkAndRequestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "Storage permission is required to add an image from your phone.",
            activity
        )
        if (storagePermissionGranted) {
            pickImage.launch("image/*")
        }
    }
}