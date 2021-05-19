package com.example.sharingang.database.cache

import androidx.lifecycle.LiveData
import com.example.sharingang.models.User
import com.example.sharingang.database.room.UserDao
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.database.store.UserStore
import com.google.maps.android.ktx.model.streetViewPanoramaOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Class to implement user repository in cache.
 * @property userDao user data access object
 * @property store user store to use
 */
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
            userDao.replace(newUsers)
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

    override suspend fun add(element: User): String? {
        return thenRefresh { store.add(element) }
    }

    override suspend fun get(id: String): User? {
        if(userDao.getUser(id) == null) {
            refreshUsers()
        }
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

    override suspend fun getChatPartners(userId: String): MutableList<String> {
        return thenRefresh { store.getChatPartners(userId) }
    }
}
