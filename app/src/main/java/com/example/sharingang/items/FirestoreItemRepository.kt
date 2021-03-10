package com.example.sharingang.items

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.BuildConfig
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreItemRepository"

@Singleton
class FirestoreItemRepository @Inject constructor() :
    ItemRepository {
    private val firestore = Firebase.firestore
    private val collectionName = "items"

    init {
        if (BuildConfig.DEBUG) {
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            firestore.useEmulator("10.0.2.2", 8080)

            // Because the Firebase emulator doesn't persist data, we disable the local persistence
            // to avoid conflicting data.
            firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        }
    }

    override suspend fun getItem(id: String): Item? {
        val document = firestore.collection(collectionName)
            .document(id)
            .get()
            .await()

        return if (document != null) {
            document.toObject(Item::class.java)
        } else {
            Log.d(TAG, "No Item with ID = $id")
            null
        }
    }

    override suspend fun getAllItems(): List<Item> {
        val result = firestore.collection(collectionName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return result.map { it.toObject(Item::class.java) }
    }

    override fun getAllItemsLiveData(): LiveData<List<Item>> {
        val query = firestore.collection(collectionName)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val itemsLiveData = MutableLiveData<List<Item>>()

        query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Failed to get all items from Firebase.", error)
            }

            itemsLiveData.value = value!!.map { it.toObject(Item::class.java) }
        }

        return itemsLiveData
    }

    override suspend fun addItem(item: Item): String? {
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
}