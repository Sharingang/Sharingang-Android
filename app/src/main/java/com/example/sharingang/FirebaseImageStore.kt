package com.example.sharingang

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.sharingang.users.CurrentUserProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseImageStore @Inject constructor(
    private val currentUserProvider: CurrentUserProvider
) : ImageStore {
    private val storage = Firebase.storage

    override suspend fun store(imageUri: Uri): Uri? {
        val userId = currentUserProvider.getCurrentUserId() ?: return null

        // /user/user-id/random-uuid.jpg
        val ref = storage.reference.child("user").child(userId)
            .child(UUID.randomUUID().toString() + ".jpg")

        val isSuccessful = ref.putFile(imageUri)
            .await()
            .task.isSuccessful

        return if (isSuccessful) ref.downloadUrl.await() else null
    }

    override suspend fun retrieve(imageUrl: Uri): Bitmap? {
        val ref = storage.getReferenceFromUrl(imageUrl.toString())
        val maxDownloadSize : Long = 1024 * 1024
        val imgBytes = ref.getBytes(maxDownloadSize).await()
        return BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
    }

}