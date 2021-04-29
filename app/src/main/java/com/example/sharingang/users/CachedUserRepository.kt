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
            val newUsers = store.getAll()
            userDao.clear()
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

    override suspend fun add(user: User): String? {
        return thenRefresh { store.add(user) }
    }

    override suspend fun get(id: String): User? {
        return userDao.getUser(id)
    }

    override suspend fun getAll(): List<User> {
        return doRefreshUsers()
    }

    override suspend fun update(user: User): Boolean {
        return thenRefresh { store.update(user) }
    }

    override suspend fun report(
        reportedUser: User,
        reporterUser: User,
        description: String,
        reason: String
    ): Boolean {
        return thenRefresh { store.report(reportedUser, reporterUser, description, reason) }
    }

    override suspend fun hasBeenReported(reporterId: String, reportedId: String): Boolean {
        return thenRefresh { store.hasBeenReported(reporterId, reportedId) }
    }
}
