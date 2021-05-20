package com.example.sharingang.database.store

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sharingang.models.Chat
import com.example.sharingang.models.User
import com.example.sharingang.ui.fragments.MessageFragment
import com.google.firebase.firestore.QuerySnapshot

/**
 * Represents a remote store of users, that we can access in some way.
 *
 * This isn't intended to be used directly in the UI (that's what an UserRepository
 * would be for), but rather to provide an abstraction over the remote fetching of users.
 */
interface UserStore {
    /**
     * Add a new user
     *
     * The user id must be the one returned by the authentication provider.
     *
     * @return id of the user if successfully added otherwise null
     */
    suspend fun add(element: User): String?

    /**
     * Returns the user with corresponding id or null if it doesn't exist
     */
    suspend fun get(id: String): User?

    /**
     * Returns all of the users that exist
     */
    suspend fun getAll(): List<User>

    /**
     * Update existing user
     *
     * The item's id cannot be null.
     *
     * @return whether the update succeeded
     */
    suspend fun update(user: User): Boolean

    /**
     * Report a user
     *
     * The reported user nor the reporter cannot be null
     *
     * @return whether the report succeeded
     */
    suspend fun report(
        reportedUser: User,
        reporterUser: User,
        description: String,
        reason: String
    ): Boolean

    /**
     * Checks if a user has already been reported by a particular user, or
     * if a user attempts to report themselves
     *
     * @return true if yes, false if no
     */
    suspend fun hasBeenReported(reporterId: String, reportedId: String): Boolean

    /**
     * Retrieves all the chat partners of a particular user
     *
     * @param userId the id of the user whose partners to retrieve
     * @return the list of all ids of the partners of the user
     */
    suspend fun getChatPartners(userId: String): MutableList<String>

    /**
     * Retrieves all messages of a user with a particular user
     *
     * @param userId the user id of the user whose messages we want to retrieve
     * @param with the target user
     * @return the resulting list of messages / Chats
     */
    suspend fun getMessages(userId: String, with: String): MutableList<Chat>

    /**
     * Puts a message into database
     *
     * @param from the sender
     * @param to the receiver
     * @param message the message
     * @return the new list of messages between the two users
     */
    suspend fun putMessage(from: String, to: String, message: String): MutableList<Chat>

    /**
     * Sets up the listener on messages for a particular user with another user
     *
     * @param userId the current user id
     * @param with the target user id
     * @param action what to do upon getting notified
     */
    suspend fun setupRefresh(userId: String, with: String, action: () -> Unit)

    /**
     * Gets the current number of unread messages of a particular user with another user
     *
     * @
     */
    suspend fun getNumUnread(userId: String, with: String): Long
}
