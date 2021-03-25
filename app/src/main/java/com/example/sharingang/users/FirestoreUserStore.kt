package com.example.sharingang.users

import com.example.sharingang.AbstractFirestoreStore
import com.google.firebase.firestore.FirebaseFirestore
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
        return super.add(user)
    }

    override suspend fun update(user: User): Boolean {
        requireNotNull(user.id)
        return super.update(user, user.id)
    }

}
