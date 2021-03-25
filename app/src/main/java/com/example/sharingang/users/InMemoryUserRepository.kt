package com.example.sharingang.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * In-memory implementation of the UserRepository
 * The data is not persisted.
 */
class InMemoryUserRepository : UserRepository {

    private val usersMap = HashMap<String, User>()

    override suspend fun getUser(id: String): User? {
        return usersMap[id]
    }

    override suspend fun getAllUsers(): List<User> {
        return usersMap.values.toList()
    }

    override fun user(id: String): LiveData<User?> {
        return MutableLiveData(usersMap[id])
    }

    override suspend fun refreshUsers() {
    }

    override suspend fun addUser(user: User): Boolean {
        requireNotNull(user.id)
        if (usersMap.containsKey(user.id)) {
            return false
        }

        usersMap[user.id] = user
        return true
    }

    override suspend fun updateUser(user: User): Boolean {
        requireNotNull(user.id)

        usersMap[user.id] = user
        return true
    }
}
