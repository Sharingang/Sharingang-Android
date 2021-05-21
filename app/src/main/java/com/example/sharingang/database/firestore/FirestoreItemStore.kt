package com.example.sharingang.database.firestore

import com.example.sharingang.models.Item
import com.example.sharingang.database.store.ItemStore
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
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

    override suspend fun getLastTimeUpdate(id: String): Date {
        return firestore.collection(DatabaseFields.DBFIELD_ITEMS)
            .document(id).get().await().getDate(DatabaseFields.DBFIELD_UPDATED_AT)!!
    }

    override suspend fun setLastTimeUpdate(id: String, newValue: Date) {
        firestore.collection(DatabaseFields.DBFIELD_ITEMS)
            .document(id).update(DatabaseFields.DBFIELD_UPDATED_AT, newValue)
    }
}
