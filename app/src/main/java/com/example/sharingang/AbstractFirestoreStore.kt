package com.example.sharingang

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

abstract class AbstractFirestoreStore<T: Any>(
    private val collectionName: String,
    private val typeClass: Class<T>,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "AbstractFirestoreStore[$collectionName]"

    open suspend fun get(id: String): T? {
        val document = firestore.collection(collectionName)
            .document(id)
            .get()
            .await()

        if (document == null) {
            Log.d(TAG, "No $collectionName with ID = $id")
        }
        return document?.toObject(typeClass)
    }

    open suspend fun getAll(): List<T> {
        val result = firestore.collection(collectionName)
            .get()
            .await()

        return result.map { it.toObject(typeClass) }
    }

    open suspend fun add(element: T): String? {
        return try {
            val document = firestore.collection(collectionName)
                .add(element)
                .await()
            Log.d(TAG, "$collectionName added with ID: ${document.id}")
            document.id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding new user to Firebase", e)
            null
        }
    }

    open suspend fun update(element: T, id: String): Boolean {
        return try {
            firestore.collection(collectionName).document(id)
                .set(element)
                .await()
            Log.d(TAG, "Updated $collectionName with LOL ID: $id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating $collectionName with ID: $id", e)
            false
        }
    }

    suspend fun delete(id: String): Boolean {
        return try {
            firestore.collection(collectionName).document(id)
                .delete()
                .await()
            Log.d(TAG, "Deleted item with ID: $id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting item with ID: $id", e)
            false
        }
    }
}
