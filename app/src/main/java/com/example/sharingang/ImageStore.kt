package com.example.sharingang

import android.graphics.Bitmap
import android.net.Uri

interface ImageStore {
    suspend fun store(imageUri: Uri): Uri?
}