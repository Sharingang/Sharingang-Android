package com.example.sharingang

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.getExternalFilesDirs
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImageAccess(
    private val registry: FragmentActivity, private val galleryCallback: (Uri?) -> Unit,
    private val cameraCallback: (Boolean?) -> Unit
) :
    DefaultLifecycleObserver {

    private var storagePermissionGranted: Boolean = false
    private var cameraPermissionGranted: Boolean = false
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var pickImage: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private lateinit var currentPhotoPath: String

    override fun onCreate(owner: LifecycleOwner) {
        requestStoragePermissionLauncher =
            registry.activityResultRegistry.register(
                "storagePermission",
                owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted)
                    Toast.makeText(
                        registry,
                        "Storage permission successfully denied. Feature is disabled.",
                        Toast.LENGTH_LONG
                    ).show()
                storagePermissionGranted = isGranted
            }
        requestCameraPermissionLauncher =
            registry.activityResultRegistry.register(
                "cameraPermission",
                owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted)
                    Toast.makeText(
                        registry,
                        "Camera permission successfully denied. Feature is disabled.",
                        Toast.LENGTH_LONG
                    ).show()
                cameraPermissionGranted = isGranted
            }
        pickImage =
            registry.activityResultRegistry.register(
                "openGallery",
                owner,
                ActivityResultContracts.GetContent(),
                galleryCallback
            )
        takePicture =
            registry.activityResultRegistry.register(
                "takePicture",
                owner,
                ActivityResultContracts.TakePicture(),
                cameraCallback
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
                    Manifest.permission.CAMERA -> cameraPermissionGranted = true
                }
            }
            shouldShowRequestPermissionRationale(context, permission) -> {
                Toast.makeText(context, rationale, Toast.LENGTH_LONG).show()
            }
            else -> {
                when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> requestStoragePermissionLauncher.launch(
                        permission
                    )
                    Manifest.permission.CAMERA -> requestCameraPermissionLauncher.launch(permission)
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

    fun openCamera(activity: Activity): Uri? {
        checkAndRequestPermission(
            Manifest.permission.CAMERA,
            "Camera permission is required to take a picture.",
            activity
        )
        if (cameraPermissionGranted) {
            val file = createImageFile(activity)
            val uri = FileProvider.getUriForFile(activity, "fileprovider", file)
            takePicture.launch(uri)
            return uri
        }
        return null
    }

    private fun createImageFile(activity: Activity): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDirs(activity, Environment.DIRECTORY_PICTURES)[0]
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}