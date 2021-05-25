package com.example.sharingang.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sharingang.models.User
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.utils.constants.NotificationFields
import com.example.sharingang.utils.notification.subscribeToTopic
import com.example.sharingang.utils.notification.unsubscribeFromTopic
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Helper class to manage user authentication with database.
 * @property context the Context
 * @property auth the authenticator to be used
 */
class AuthHelper(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val authUI: AuthUI,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val userRepository: UserRepository,
    fragment: Fragment,
    private val currentUserProvider: CurrentUserProvider,
    private val execUponSignInSuccess: (FirebaseUser, String) -> Unit
) {

    private lateinit var currentUser: FirebaseUser
    private lateinit var currentUserId: String

    private val resultLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentUserId = currentUserProvider.getCurrentUserId()!!
                currentUser = auth.currentUser!!
                addUserToDatabase(currentUser)
                execUponSignInSuccess(currentUser, currentUserId)
            }
        }


    /**
     * Sign in.
     */
    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = authUI
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        resultLauncher.launch(intent)
        subscribeToTopic(NotificationFields.CHAT_TOPIC)
    }

    /**
     * Sign out.
     */
    fun signOut() {
        unsubscribeFromTopic(NotificationFields.CHAT_TOPIC)
        authUI.signOut(context)
    }

    private fun addUserToDatabase(user: FirebaseUser) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (userRepository.get(user.uid) == null) {
                userRepository.add(
                    User(user.uid, user.displayName!!, user.photoUrl?.toString())
                )
            }
        }
    }
}
