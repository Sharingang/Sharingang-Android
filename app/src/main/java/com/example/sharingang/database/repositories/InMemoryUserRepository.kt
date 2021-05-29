package com.example.sharingang.database.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.models.Chat
import com.example.sharingang.models.User
import java.util.*
import kotlin.collections.HashMap

/**
 * In-memory implementation of the UserRepository
 * The data is not persisted.
 */
class InMemoryUserRepository : UserRepository {

    private val usersMap = HashMap<String, User>()

    private val chatPartnersMap = hashMapOf<String, MutableList<String>>()
    private val messagesMap = hashMapOf<String, HashMap<String, MutableList<Chat>>>()
    private val numUnreadMap = hashMapOf<String, HashMap<String, Long>>()
    private val blocks = hashMapOf<String, MutableList<String>>()
    private val blockInfo = hashMapOf<String, HashMap<String, Pair<String, String>>>()

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

    override suspend fun getChatPartners(userId: String): List<String> {
        val partners = chatPartnersMap[userId]
        if (partners == null) {
            chatPartnersMap[userId] = mutableListOf()
        }
        return chatPartnersMap[userId]!!
    }

    override suspend fun getMessages(userId: String, with: String): List<Chat> {
        val allMessages = messagesMap[userId]
        numUnreadMap[userId] = hashMapOf(with to 0)
        if (allMessages != null) {
            val messages = allMessages[with]
            return messages ?: mutableListOf()
        }
        return mutableListOf()
    }

    override suspend fun putMessage(from: String, to: String, message: String, date: Date): List<Chat> {
        val chat = Chat(from, to, message, date)
        if (!messagesMap.containsKey(from) || !messagesMap.containsKey(to)) {
            messagesMap[from] = hashMapOf(to to mutableListOf(chat))
            messagesMap[to] = hashMapOf(from to mutableListOf(chat))
        } else {
            messagesMap[from]!![to]!!.add(chat)
            messagesMap[to]!![from]!!.add(chat)
        }
        if (!chatPartnersMap.containsKey(from) || !messagesMap.containsKey(to)) {
            chatPartnersMap[from] = mutableListOf(to)
            chatPartnersMap[to] = mutableListOf(from)
        } else if (!chatPartnersMap[from]!!.contains(to) && !chatPartnersMap[to]!!.contains(from)) {
            chatPartnersMap[from]!!.add(to)
            chatPartnersMap[to]!!.add(from)
        }
        updateUnreads(from, to)
        return messagesMap[from]!![to]!!
    }

    override suspend fun setupConversationRefresh(
        userId: String, with: String, action: () -> Unit
    ) {
        return
    }

    override suspend fun getNumUnread(userId: String, with: String): Long {
        val userEntry = numUnreadMap[userId]
        if (userEntry != null) {
            return userEntry[with]!!
        }
        return 0
    }

    override suspend fun clearNumUnread(userId: String, with: String) {
        numUnreadMap[userId] = hashMapOf(with to 0)
    }

    override suspend fun block(
        blockerId: String,
        blockedId: String,
        reason: String,
        description: String
    ) {
        if(blocks[blockerId] == null) {
            blocks[blockerId] = mutableListOf(blockedId)
        }
        else blocks[blockerId]!!.add(blockedId)
        blockInfo[blockerId] = hashMapOf(blockedId to Pair(reason, description))
    }

    override suspend fun hasBeenBlocked(userId: String, by: String): Boolean {
        return if(blocks[by] == null) false else blocks[by]!!.contains(userId)
    }

    override suspend fun getBlockedUsers(userId: String): List<String> {
        return if(blocks[userId] == null) listOf() else blocks[userId]!!
    }

    override suspend fun getBlockInformation(blockerId: String, blockedId: String): String {
        val reason = blockInfo[blockerId]!![blockedId]!!.first
        val description = blockInfo[blockerId]!![blockedId]!!.second
        return "Reason: $reason\nDescription: $description"
    }

    override suspend fun unblock(blockerId: String, blockedId: String) {
        blocks[blockerId]!!.remove(blockedId)
    }

    /**
     * Update the number of unread messages of two messaging users
     *
     * @param from the sender
     * @param to the receiver
     */
    private suspend fun updateUnreads(from: String, to: String) {
        numUnreadMap[from] = hashMapOf(to to 0)
        numUnreadMap[to] =
            if (!numUnreadMap.containsKey(to)) hashMapOf(from to 1)
            else hashMapOf(from to getNumUnread(to, from) + 1)
    }

}
