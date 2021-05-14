package com.example.sharingang.users

/**
 * Interface to implement basic functionality of a current user provider.
 */
interface CurrentUserProvider {
    /**
     * @return Current signed in user's id.
     */
    fun getCurrentUserId(): String?

    /**
     * @return Current signed in user's email.
     */
    fun getCurrentUserEmail(): String?

    /**
     * @return Current signed in user's name.
     */
    fun getCurrentUserName(): String?
}
