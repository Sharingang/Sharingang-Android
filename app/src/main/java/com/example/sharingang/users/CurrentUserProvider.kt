package com.example.sharingang.users

import com.google.firebase.auth.FirebaseUser

interface CurrentUserProvider {
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
    fun getCurrentUser(): FirebaseUser?
}
