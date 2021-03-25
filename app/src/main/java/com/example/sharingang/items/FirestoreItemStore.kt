package com.example.sharingang.items

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreItemStore"

/**
 * Implementation of ItemRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreItemStore @Inject constructor(private val firestore: FirebaseFirestore) : ItemStore {
    private val collectionName = "items"

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
