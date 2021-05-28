package com.example.sharingang.database.store

import com.example.sharingang.models.Chat
import com.example.sharingang.models.User
import java.util.*

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
    suspend fun getChatPartners(userId: String): List<String>

    /**
     * Retrieves all messages of a user with a particular user
     *
     * @param userId the user id of the user whose messages we want to retrieve
     * @param with the target user
     * @return the resulting list of messages / Chats
     */
    suspend fun getMessages(userId: String, with: String): List<Chat>

    /**
     * Puts a message into database
     *
     * @param from the sender
     * @param to the receiver
     * @param message the message
     * @return the new list of messages between the two users
     */
    suspend fun putMessage(from: String, to: String, message: String, date: Date): List<Chat>

    /**
     * Sets up the listener on messages for a particular user with another user
     *
     * @param userId the current user id
     * @param with the target user id
     * @param action what to do upon getting notified
     */
    suspend fun setupConversationRefresh(userId: String, with: String, action: () -> Unit)

    /**
     * Gets the current number of unread messages of a particular user with another user
     *
     * @param userId the user whose number of unread messages we want to get
     * @param with the target user
     * @return the number of unread messages
     */
    suspend fun getNumUnread(userId: String, with: String): Long

    /**
     * Clears the number of unread messages of a user with another user
     *
     * @param userId the user whose number of unread messages we want to clear
     * @param with the target user
     */
    suspend fun clearNumUnread(userId: String, with: String)

    /**
     * Blocks a user by another particular user
     *
     * @param blockerId the user who wants to block the other
     * @param blockedId the target user
     * @param reason the reason for which a user is blocking another
     * @param description the description given by the blocker
     */
    suspend fun block(blockerId: String, blockedId: String, reason: String, description: String)

    /**
     * Checks if a user has already been blocked by another particular user
     *
     * @param userId the user to check if they were blocked
     * @param by the potential blocker user id
     * @return whether the user has already been blocked
     */
    suspend fun hasBeenBlocked(userId: String, by: String): Boolean

    /**
     * Retrieves the list of blocked users of a particular user
     *
     * @param userId the user whose list we want to retrieve
     * @return the list of all blocked users
     */
    suspend fun getBlockedUsers(userId: String): List<String>

}
