package com.example.sharingang.users

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CachedUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val store: UserStore
) : UserRepository {

    override fun user(id: String): LiveData<User?> {
        return userDao.getUserLiveData(id)
    }

    private suspend fun doRefreshUsers(): List<User> =
        // This is necessary, since you want to avoid doing this work on the main thread
        withContext(Dispatchers.IO) {
            val newUsers = store.getAllUsers()
            userDao.insert(newUsers)
            newUsers
        }


    override suspend fun refreshUsers() {
        doRefreshUsers()
    }

    private suspend fun <T> thenRefresh(fn: suspend () -> T): T {
        val ret = fn()
        refreshUsers()
        return ret
    }

    override suspend fun addUser(user: User): Boolean {
        return thenRefresh { store.addUser(user) }
    }

    override suspend fun getUser(id: String): User? {
        return userDao.getUser(id)
    }

    override suspend fun getAllUsers(): List<User> {
        return doRefreshUsers()
    }

    override suspend fun updateUser(user: User): Boolean {
        return thenRefresh { store.updateUser(user) }
    }
}
