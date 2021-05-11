package com.example.sharingang

import android.net.Uri

/**
 * Interface to implement the image storage on Firebase.
 */
interface ImageStore {
    suspend fun store(imageUri: Uri): Uri?
}