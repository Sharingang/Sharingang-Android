package com.example.sharingang.users

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirestoreCurrentUserProvider @Inject constructor(
    private val auth: FirebaseAuth
) : CurrentUserProvider {

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
