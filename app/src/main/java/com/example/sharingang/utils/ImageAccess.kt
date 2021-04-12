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
    private val activity: FragmentActivity
) :
    DefaultLifecycleObserver {

    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var pickImage: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private var cameraUri: Uri? = null
    private var imageUri: Uri? = null

    private lateinit var imageView: ImageView

    override fun onCreate(owner: LifecycleOwner) {
        setupPermissionLaunchers(owner)
        pickImage = activity.activityResultRegistry.register(
            "openGallery",
            owner,
            ActivityResultContracts.GetContent(),
            ::galleryCallback
        )
        takePicture = activity.activityResultRegistry.register(
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
        return activity.activityResultRegistry.register(
            key,
            owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted)
                Toast.makeText(
                    activity,
                    "$permission permission successfully denied. Feature is disabled.",
                    Toast.LENGTH_LONG
                ).show()
            else
                launchCorrectImageAccess(permission)
        }
    }

    private fun launchCorrectImageAccess(permission: String) {
        when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE -> pickImage.launch("image/*")
            Manifest.permission.CAMERA -> launchCamera()
        }
    }

    private fun checkAndRequestPermission(permission: String, rationale: String) {
        if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCorrectImageAccess(permission)
        } else {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                Toast.makeText(activity, rationale, Toast.LENGTH_LONG).show()
            }
            when (permission) {
                Manifest.permission.READ_EXTERNAL_STORAGE -> requestStoragePermissionLauncher.launch(
                    permission
                )
                Manifest.permission.CAMERA -> requestCameraPermissionLauncher.launch(permission)
            }
        }
    }

    private fun launchCamera() {
        val file = createImageFile(activity)
        val uri =
            FileProvider.getUriForFile(activity, "com.example.sharingang.fileprovider", file)
        takePicture.launch(uri)
        cameraUri = uri
    }

    fun openGallery() {
        checkAndRequestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "Storage permission is required to add an image from your phone."
        )
    }

    fun openCamera() {
        checkAndRequestPermission(
            Manifest.permission.CAMERA,
            "Camera permission is required to take a picture."
        )
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