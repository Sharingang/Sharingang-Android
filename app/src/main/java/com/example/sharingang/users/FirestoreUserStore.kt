package com.example.sharingang.users

import android.util.Log
import com.example.sharingang.AbstractFirestoreStore
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
    UserStore, AbstractFirestoreStore<User>("users", User::class.java, firestore) {

    override suspend fun add(user: User): String? {
        requireNotNull(user.id)
        return if (super.update(user, user.id)) {
            user.id
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
                .collection("users").document(reportedUser.id!!)
                .collection("reports").document(reporterUser.id!!).set(
                    hashMapOf(
                        "reporter" to reporterUser.id,
                        "reason" to reason,
                        "description" to description,
                        "reportedAt" to Date()
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
        val docIdRef = firestore.collection("users").document(reportedId)
            .collection("reports").document(reporterId).get().await()
        return docIdRef.exists() || reportedId == reporterId
    }
}
