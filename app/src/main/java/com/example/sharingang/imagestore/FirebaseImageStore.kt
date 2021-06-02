package com.example.sharingang.imagestore

import android.net.Uri
import com.example.sharingang.auth.CurrentUserProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

/**
 * Class used to store images on Firebase.
 * @property currentUserProvider the current user provider
 */
class FirebaseImageStore @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val storage: FirebaseStorage
) : ImageStore {

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
}
