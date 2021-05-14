package com.example.sharingang.database.firestore

import com.example.sharingang.models.Item
import com.example.sharingang.database.store.ItemStore
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ItemRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreItemStore @Inject constructor(firestore: FirebaseFirestore) :
    ItemStore, AbstractFirestoreStore<Item>("items", Item::class.java, firestore) {

    override suspend fun set(item: Item): String? {
        return if (item.id == null) {
            super.add(item)
        } else {
            if (super.update(item, item.id)) {
                item.id
            } else {
                null
            }
        }
    }
}
