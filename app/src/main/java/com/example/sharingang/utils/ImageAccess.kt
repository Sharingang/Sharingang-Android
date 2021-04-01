package com.example.sharingang.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImageAccess(
    private val registry: FragmentActivity
) :
    DefaultLifecycleObserver {

    private var storagePermissionGranted: Boolean = false
    private var cameraPermissionGranted: Boolean = false
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var pickImage: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private var cameraUri: Uri? = null
    private var imageUri: Uri? = null

    private lateinit var imageView: ImageView

    override fun onCreate(owner: LifecycleOwner) {
        setupPermissionLaunchers(owner)
        pickImage =
            registry.activityResultRegistry.register(
                "openGallery",
                owner,
                ActivityResultContracts.GetContent(),
                ::galleryCallback
            )
        takePicture =
            registry.activityResultRegistry.register(
                "takePicture",
                owner,
                ActivityResultContracts.TakePicture(),
                ::cameraCallback
            )
    }

    fun unregister() {
        pickImage.unregister()
        requestStoragePermissionLauncher.unregister()
        takePicture.unregister()
        requestCameraPermissionLauncher.unregister()
    }

    fun setupImageView(view: ImageView) {
        imageView = view
    }

    private fun setupPermissionLaunchers(owner: LifecycleOwner) {
        requestStoragePermissionLauncher = createPermissionResultRegistry(
            "storagePermission",
            owner,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        requestCameraPermissionLauncher =
            createPermissionResultRegistry("cameraPermission", owner, Manifest.permission.CAMERA)
    }

    private fun createPermissionResultRegistry(
        key: String,
        owner: LifecycleOwner,
        permission: String
    ): ActivityResultLauncher<String> {
        return registry.activityResultRegistry.register(
            key,
            owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted)
                Toast.makeText(
                    registry,
                    "$permission permission successfully denied. Feature is disabled.",
                    Toast.LENGTH_LONG
                ).show()
            when (permission) {
                Manifest.permission.CAMERA -> cameraPermissionGranted = isGranted
                Manifest.permission.READ_EXTERNAL_STORAGE -> storagePermissionGranted = isGranted
            }
        }
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

    fun openCamera(activity: Activity) {
        checkAndRequestPermission(
            Manifest.permission.CAMERA,
            "Camera permission is required to take a picture.",
            activity
        )
        if (cameraPermissionGranted) {
            val file = createImageFile(activity)
            val uri =
                FileProvider.getUriForFile(activity, "com.example.sharingang.fileprovider", file)
            takePicture.launch(uri)
            cameraUri = uri
        }
    }

    private fun createImageFile(activity: Activity): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDirs(activity, Environment.DIRECTORY_PICTURES)[0]
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun galleryCallback(uri: Uri?) {
        uri?.let {
            imageUri = uri
            imageView.setImageURI(uri)
        }
    }

    private fun cameraCallback(res: Boolean?) {
        res?.let {
            if (it) {
                cameraUri?.let {
                    imageView.setImageURI(cameraUri)
                    imageUri = cameraUri
                }
            }
        }
    }

    fun getImageUri(): Uri? {
        return imageUri
    }
}