package com.example.sharingang

import android.net.Uri

/**
 * Interface to implement the image storage on a remote server accessible with HTTP.
 */
interface ImageStore {
    suspend fun store(imageUri: Uri): Uri?
}