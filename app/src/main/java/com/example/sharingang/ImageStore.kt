package com.example.sharingang

import android.net.Uri

interface ImageStore {
    suspend fun store(imageUri: Uri): Uri?
}