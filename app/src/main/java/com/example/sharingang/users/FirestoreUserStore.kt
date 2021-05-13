package com.example.sharingang.users

import android.util.Log
import com.example.sharingang.AbstractFirestoreStore
import com.example.sharingang.utils.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "FirestoreUserStore"

/**
 * Implementation of UserRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreUserStore @Inject constructor(private val firestore: FirebaseFirestore) :
    UserStore, AbstractFirestoreStore<User>(DatabaseFields.DBFIELD_USERS,
    User::class.java, firestore) {

    override suspend fun add(element: User): String? {
        requireNotNull(element.id)
        return if (super.update(element, element.id)) {
            element.id
        } else {
            null
        }
    }

    override suspend fun update(user: User): Boolean {
        requireNotNull(user.id)
        return super.update(user, user.id)
    }

    override suspend fun report(
        reportedUser: User,
        reporterUser: User,
        description: String,
        reason: String
    ): Boolean {
        return try {
            firestore
                .collection(DatabaseFields.DBFIELD_USERS).document(reportedUser.id!!)
                .collection(DatabaseFields.DBFIELD_REPORTS).document(reporterUser.id!!).set(
                    hashMapOf(
                        DatabaseFields.DBFIELD_REPORTER to reporterUser.id,
                        DatabaseFields.DBFIELD_REASON to reason,
                        DatabaseFields.DBFIELD_DESCRIPTION to description,
                        DatabaseFields.DBFIELD_REPORTEDAT to Date()
                    )
                )
            Log.d(TAG, "User ${reporterUser.id} reported ${reportedUser.id}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "User ${reporterUser.id} reported ${reportedUser.id}")
            false
        }
    }

    override suspend fun hasBeenReported(reporterId: String, reportedId: String): Boolean {
        val docIdRef = firestore.collection(DatabaseFields.DBFIELD_USERS).document(reportedId)
            .collection(DatabaseFields.DBFIELD_REPORTS).document(reporterId).get().await()
        return docIdRef.exists()
    }
}
