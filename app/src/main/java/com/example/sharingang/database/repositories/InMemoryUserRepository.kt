package com.example.sharingang.database.repositories

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.models.Chat
import com.example.sharingang.models.User
import com.example.sharingang.ui.fragments.MessageFragment

/**
 * In-memory implementation of the UserRepository
 * The data is not persisted.
 */
class InMemoryUserRepository : UserRepository {

    private val usersMap = HashMap<String, User>()

    private val chatPartnersMap = HashMap<String, MutableList<String>>()
    private val messagesMap = HashMap<String, HashMap<String, MutableList<Chat>>>()

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

    override suspend fun add(element: User): String? {
        requireNotNull(element.id)
        if (usersMap.containsKey(element.id)) {
            return null
        }

        usersMap[element.id] = element
        return element.id
    }

    override suspend fun update(user: User): Boolean {
        requireNotNull(user.id)

        usersMap[user.id] = user
        return true
    }

    override suspend fun report(
        reportedUser: User,
        reporterUser: User,
        description: String,
        reason: String
    ): Boolean {
        return true
    }

    override suspend fun hasBeenReported(reporterId: String, reportedId: String): Boolean {
        return false
    }

    override suspend fun getChatPartners(userId: String): MutableList<String> {
        return chatPartnersMap[userId] ?: mutableListOf()
    }

    override suspend fun getMessages(userId: String, with: String): MutableList<Chat> {
        val allMessages = messagesMap[userId]
        if(allMessages != null) {
            val messages = allMessages[with]
            return messages ?: mutableListOf()
        }
        return mutableListOf()
    }

    override suspend fun putMessage(from: String, to: String, message: String): MutableList<Chat> {
        messagesMap[from]!![to]!!.add(Chat(from = from, to = to, message = message))
        return messagesMap[from]!![to]!!
    }

    override suspend fun setupRefresh(
        userId: String, with: String, action: () -> Unit, lifecycleScope: LifecycleCoroutineScope
    ) { return }

}
