package com.example.sharingang.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthHelper (
    private val context: Context,
    private val auth: FirebaseAuth,
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


    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        resultLauncher.launch(intent)
    }

    fun signOut() {
        AuthUI.getInstance().signOut(context)
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