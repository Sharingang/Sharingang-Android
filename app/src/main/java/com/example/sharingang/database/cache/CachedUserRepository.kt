package com.example.sharingang.database.cache

import androidx.lifecycle.LiveData
import com.example.sharingang.models.User
import com.example.sharingang.database.room.UserDao
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.database.store.UserStore
import com.example.sharingang.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
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
        if (userDao.getUser(id) == null) {
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

    override suspend fun getChatPartners(userId: String): List<String> {
        return store.getChatPartners(userId)
    }

    override suspend fun getMessages(userId: String, with: String): List<Chat> {
        return store.getMessages(userId, with)
    }

    override suspend fun putMessage(from: String, to: String, message: String, date: Date): List<Chat> {
        return store.putMessage(from, to, message, date)
    }

    override suspend fun setupConversationRefresh(
        userId: String,
        with: String,
        action: () -> Unit
    ) {
        return store.setupConversationRefresh(userId, with, action)
    }

    override suspend fun getNumUnread(userId: String, with: String): Long {
        return store.getNumUnread(userId, with)
    }

    override suspend fun clearNumUnread(userId: String, with: String) {
        return store.clearNumUnread(userId, with)
    }

    override suspend fun block(
        blockerId: String,
        blockedId: String,
        reason: String,
        description: String
    ) {
        return thenRefresh { store.block(blockerId, blockedId, reason, description) }
    }

    override suspend fun hasBeenBlocked(userId: String, by: String): Boolean {
        return thenRefresh { store.hasBeenBlocked(userId, by) }
    }

    override suspend fun getBlockedUsers(userId: String): List<String> {
        return thenRefresh { store.getBlockedUsers(userId) }
    }

}
