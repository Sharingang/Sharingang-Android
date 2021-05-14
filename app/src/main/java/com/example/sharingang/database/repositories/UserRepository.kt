package com.example.sharingang.database.repositories

import androidx.lifecycle.LiveData
import com.example.sharingang.models.User
import com.example.sharingang.database.store.UserStore

/**
 * Represents a repository for accessing users
 *
 * This is suitable for use directly in the UI, since it provides LiveData methods,
 * and the ability to be refreshed according to UI actions.
 */
interface UserRepository : UserStore {
    /**
     * Returns the user with corresponding id or null if it doesn't exist, as LiveData
     */
    fun user(id: String): LiveData<User?>

    /**
     * Make sure that the repository holds up to date items
     */
    suspend fun refreshUsers()
}
