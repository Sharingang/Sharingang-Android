package com.example.sharingang.items

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "FirestoreItemRepository"

class FirestoreItemRepository : ItemRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionName = "items"

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
                Log.w(TAG, "Error adding new item to Firebase", e)
            }
    }
}