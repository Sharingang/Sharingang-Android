package com.example.sharingang.items

import com.example.sharingang.AbstractFirestoreStore
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ItemRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreItemStore @Inject constructor(private val firestore: FirebaseFirestore) :
    ItemStore, AbstractFirestoreStore<Item>("items", Item::class.java, firestore) {

    override suspend fun add(item: Item): String? {
        require(item.id == null)
        return super.add(item)
    }

    override suspend fun update(item: Item): Boolean {
        requireNotNull(item.id)
        return super.update(item, item.id)
    }

}
