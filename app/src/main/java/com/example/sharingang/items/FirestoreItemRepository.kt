package com.example.sharingang.items

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

private const val TAG = "FirestoreItemRepository"

class FirestoreItemRepository(useFirebaseEmulator: Boolean = false) : ItemRepository {
    private val firestore = Firebase.firestore
    private val collectionName = "items"

    init {
        if (useFirebaseEmulator) {
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

    override fun getAllItems(): LiveData<List<Item>> {
        val query = firestore.collection(collectionName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)

        val itemsLiveData = MutableLiveData<List<Item>>()

        query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Failed to get all items from Firebase.", error)
            }

            itemsLiveData.value = value!!.map { it.toObject(Item::class.java) }
        }

        return itemsLiveData
    }

    override fun addItem(item: Item) {
        firestore.collection(collectionName)
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Item added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding new item to Firebase", e)
            }
    }
}