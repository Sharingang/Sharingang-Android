package com.example.sharingang.database.store

import com.example.sharingang.models.User

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
}
