package com.example.sharingang.database.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.models.Chat
import com.example.sharingang.models.User

/**
 * In-memory implementation of the UserRepository
 * The data is not persisted.
 */
class InMemoryUserRepository : UserRepository {

    private val usersMap = HashMap<String, User>()

    private var chatPartnersMap = hashMapOf<String, MutableList<String>>()
    private var messagesMap = hashMapOf<String, HashMap<String, MutableList<Chat>>>()
    private var numUnreadMap = hashMapOf<String,HashMap<String, Long>>()

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
        val partners = chatPartnersMap[userId]
        if(partners == null) {
            chatPartnersMap[userId] = mutableListOf()
        }
        return chatPartnersMap[userId]!!
    }

    override suspend fun getMessages(userId: String, with: String): MutableList<Chat> {
        val allMessages = messagesMap[userId]
        numUnreadMap[userId] = hashMapOf(with to 0)
        if(allMessages != null) {
            val messages = allMessages[with]
            return messages ?: mutableListOf()
        }
        return mutableListOf()
    }

    override suspend fun putMessage(from: String, to: String, message: String): MutableList<Chat> {
        val chat = Chat(from, to, message)
        if(!messagesMap.containsKey(from) || !messagesMap.containsKey(to)) {
            messagesMap[from] = hashMapOf(to to mutableListOf(chat))
            messagesMap[to] = hashMapOf(from to mutableListOf(chat))
            Log.d("inMem", "was empty; now contains ${messagesMap[from]!![to]!!.size} elements")
        }
        else {
            Log.d("inMem", "contains ${messagesMap[from]!![to]!!.size} elements")
            messagesMap[from]!![to]!!.add(chat)
            messagesMap[to]!![from]!!.add(chat)
        }
        if(!chatPartnersMap.containsKey(from) || !messagesMap.containsKey(to)) {
            chatPartnersMap[from] = mutableListOf(to)
            chatPartnersMap[to] = mutableListOf(from)
        }
        else if(!chatPartnersMap[from]!!.contains(to) && !chatPartnersMap[to]!!.contains(from)) {
            chatPartnersMap[from]!!.add(to)
            chatPartnersMap[to]!!.add(from)
        }

        numUnreadMap[from] = hashMapOf(to to 0)
        numUnreadMap[to] =
            if(!numUnreadMap.containsKey(to)) hashMapOf(from to 1)
            else hashMapOf(from to getNumUnread(to, from) + 1)

        Log.d("inMem: ","${messagesMap[from]!![to]!!}")
        return messagesMap[from]!![to]!!
    }

    override suspend fun setupRefresh(
        userId: String, with: String, action: () -> Unit) {
        return
    }

    override suspend fun getNumUnread(userId: String, with: String): Long {
        val userEntry = numUnreadMap[userId]
        if(userEntry != null){
            return userEntry[with]!!
        }
        return 0
    }

    override suspend fun clearNumUnread(userId: String, with: String) {
        numUnreadMap[userId] = hashMapOf(with to 0)
    }

}
