package com.example.sharingang.users

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * Class used to retrieve the current signed in user.
 * @property auth the authenticator to use
 */
class FirestoreCurrentUserProvider @Inject constructor(
    private val auth: FirebaseAuth
) : CurrentUserProvider {

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    override fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }
}
