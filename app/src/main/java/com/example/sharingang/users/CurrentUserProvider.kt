package com.example.sharingang.users

interface CurrentUserProvider {
    fun getCurrentUserId(): String?
}
