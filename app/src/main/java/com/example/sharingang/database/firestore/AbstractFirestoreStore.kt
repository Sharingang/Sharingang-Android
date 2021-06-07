package com.example.sharingang.database.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Abstract class to implement basic database usage.
 * @param[T] Type of element to use for database.
 * @property collectionName The name of the collection
 * @property typeClass Class with type T
 * @property firestore instance of FirebaseFirestore
 */
abstract class AbstractFirestoreStore<T : Any>(
    private val collectionName: String,
    private val typeClass: Class<T>,
    private val firestore: FirebaseFirestore
) {
    private val tag = "AbstractFirestoreStore[$collectionName]"

    /**
     * Function to retrieve an element of the database.
     * @param[id] the id of the element.
     * @return The item corresponding to the id.
     */
    open suspend fun get(id: String): T? {
        return try {
            val document = firestore.collection(collectionName)
                .document(id)
                .get()
                .await()

            if (document == null) {
                Log.d(tag, "No $collectionName with ID = $id")
            }
            document?.toObject(typeClass)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Function to retrieve all elements in the database.
     * @return List of all elements in the database.
     */
    open suspend fun getAll(): List<T> {
        return try {
            val result = firestore.collection(collectionName)
                .get()
                .await()

            result.map { it.toObject(typeClass) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Function to add an element to the database.
     * @param[element] The element to add to the database.
     * @return The id associated to that element.
     */
    open suspend fun add(element: T): String? {
        return try {
            val document = firestore.collection(collectionName)
                .add(element)
                .await()
            Log.d(tag, "$collectionName added with ID: ${document.id}")
            document.id
        } catch (e: Exception) {
            Log.e(tag, "Error adding new user to Firebase", e)
            null
        }
    }

    /**
     * Function to update an existing element in database.
     * @param[element] The element to update.
     * @param[id] The id of the element in the database.
     * @return Boolean indicating success of the operation.
     */
    open suspend fun update(element: T, id: String): Boolean {
        return try {
            firestore.collection(collectionName).document(id)
                .set(element)
                .await()
            Log.d(tag, "Updated $collectionName with ID: $id")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error updating $collectionName with ID: $id", e)
            false
        }
    }

    /**
     * Function to remove an element from the database.
     * @param[id] The id of the element to remove.
     * @return Boolean indicating success of the operation.
     */
    suspend fun delete(id: String): Boolean {
        return try {
            firestore.collection(collectionName).document(id)
                .delete()
                .await()
            Log.d(tag, "Deleted item with ID: $id")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error deleting item with ID: $id", e)
            false
        }
    }
}
