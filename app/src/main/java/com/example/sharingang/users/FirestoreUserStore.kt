package com.example.sharingang.users

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreUserStore"

/**
 * Implementation of UserRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreUserStore @Inject constructor(private val firestore: FirebaseFirestore) :
    UserStore {
    private val collectionName = "users"

    override suspend fun getUser(id: String): User? {
        val document = firestore.collection(collectionName)
            .document(id)
            .get()
            .await()

        if (document == null) {
            Log.d(TAG, "No User with ID = $id")
        }
        return document?.toObject(User::class.java)
    }

    override suspend fun getAllUsers(): List<User> {
        val result = firestore.collection(collectionName)
            .get()
            .await()

        return result.map { it.toObject(User::class.java) }
    }

    override suspend fun addUser(user: User): Boolean {
        requireNotNull(user.id)

        return try {
            val document = firestore.collection(collectionName)
                .add(user)
                .await()
            Log.d(TAG, "User added with ID: ${document.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding new user to Firebase", e)
            false
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        requireNotNull(user.id)

        return try {
            firestore.collection(collectionName).document(user.id)
                .set(user)
                .await()
            Log.d(TAG, "Updated user with ID: ${user.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user with ID: ${user.id}", e)
            false
        }
    }
}
