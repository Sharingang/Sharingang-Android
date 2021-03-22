package com.example.sharingang.items

import android.util.Log
import com.example.sharingang.BuildConfig
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreItemRepository"

/**
 * Implementation of ItemRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
class FirestoreItemStore : ItemStore {
    private val firestore = Firebase.firestore
    private val collectionName = "items"

    init {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Using Firebase emulator.")
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            firestore.useEmulator("10.0.2.2", 8080)

            // Because the Firebase emulator doesn't persist data, we disable the local persistence
            // to avoid conflicting data.
            firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        } else {
            Log.d(TAG, "Using production Firebase.")
        }
    }

    override suspend fun getItem(id: String): Item? {
        val document = firestore.collection(collectionName)
            .document(id)
            .get()
            .await()

        if (document == null) {
            Log.d(TAG, "No Item with ID = $id")
        }
        return document?.toObject(Item::class.java)
    }

    override suspend fun getAllItems(): List<Item> {
        val result = firestore.collection(collectionName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return result.map { it.toObject(Item::class.java) }
    }

    override suspend fun addItem(item: Item): String? {
        require(item.id == null)

        return try {
            val document = firestore.collection(collectionName)
                .add(item)
                .await()
            Log.d(TAG, "Item added with ID: ${document.id}")
            document.id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding new item to Firebase", e)
            null
        }
    }

    override suspend fun updateItem(item: Item): Boolean {
        requireNotNull(item.id)

        return try {
            firestore.collection(collectionName).document(item.id)
                .set(item)
                .await()
            Log.d(TAG, "Updated item with ID: ${item.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating item with ID: ${item.id}", e)
            false
        }
    }

    override suspend fun deleteItem(id: String): Boolean {
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