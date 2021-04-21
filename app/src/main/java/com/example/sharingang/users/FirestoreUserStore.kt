package com.example.sharingang.users

import android.util.Log
import com.example.sharingang.AbstractFirestoreStore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    override suspend fun report(reportedUser: User, reporterUser: User): Boolean {
        return try {
            firestore
                .collection("users").document(reportedUser.id!!)
                .collection("reports").document(reporterUser.id!!).set(
                    hashMapOf(
                        reporterUser.id to Calendar.getInstance().time.toString()
                    )
                )
            Log.d("TESTING FIRE", "User ${reporterUser.id} reported ${reportedUser.id}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TESTING FIRE", "Error reporting user ${reportedUser.id} by ${reporterUser.id}")
            false
        }
    }

}
