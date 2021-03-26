package com.example.sharingang.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * In-memory implementation of the UserRepository
 * The data is not persisted.
 */
class InMemoryUserRepository : UserRepository {

    private val usersMap = HashMap<String, User>()

    override suspend fun get(id: String): User? {
        return usersMap[id]
    }

    override suspend fun getAll(): List<User> {
        return usersMap.values.toList()
    }

    override fun user(id: String): LiveData<User?> {
        return MutableLiveData(usersMap[id])
    }

    override suspend fun refreshUsers() {
    }

    override suspend fun add(user: User): String? {
        requireNotNull(user.id)
        if (usersMap.containsKey(user.id)) {
            return null
        }

        usersMap[user.id] = user
        return user.id
    }

    override suspend fun update(user: User): Boolean {
        requireNotNull(user.id)

        usersMap[user.id] = user
        return true
    }
}
