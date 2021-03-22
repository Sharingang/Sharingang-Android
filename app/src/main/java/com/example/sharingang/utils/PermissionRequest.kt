package com.example.sharingang.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun requestPermissionLauncher(
    fragment: Fragment,
    callback: () -> Unit
): ActivityResultLauncher<String> {
    return fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            callback()
        }
    }
}

fun doOrGetPermission(
    context: Context,
    fragment: Fragment,
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
        requestPermission(context, fragment, permission, resultLauncher!!)
    }
}

fun requestPermission(
    context: Context,
    fragment: Fragment,
    permission: String,
    resultLauncher: ActivityResultLauncher<String>
) {
    if (fragment.shouldShowRequestPermissionRationale(permission)) {
        Toast.makeText(
            context,
            String.format("%s permission is required", permission),
            Toast.LENGTH_LONG
        ).show()
    }
    resultLauncher.launch(permission)
}