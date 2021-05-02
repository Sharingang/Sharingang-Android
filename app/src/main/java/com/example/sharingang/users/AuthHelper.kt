package com.example.sharingang.users

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleCoroutineScope
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthHelper (
    private val resultLauncher: ActivityResultLauncher<Intent>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val currentUser: FirebaseUser?,
    val userRepository: UserRepository

    ) {

    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        resultLauncher.launch(intent)
    }

    fun restoreLoginStatus() {
        if (currentUser != null) addUserToDatabase(currentUser!!)
    }

    fun addUserToDatabase(user: FirebaseUser) {
        val userToConnectId = user.uid
        lifecycleScope.launch(Dispatchers.IO) {
            if (userRepository.get(userToConnectId) == null) {
                userRepository.add(
                    User(
                        id = userToConnectId,
                        name = user.displayName!!,
                        profilePicture = user.photoUrl?.toString()
                    )
                )
            }
        }
    }
}