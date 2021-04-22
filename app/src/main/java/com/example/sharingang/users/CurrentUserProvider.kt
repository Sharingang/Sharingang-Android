package com.example.sharingang.users

interface CurrentUserProvider {
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
}
