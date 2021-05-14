package com.example.sharingang.database.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sharingang.models.User

/**
 * Dao for a user database.
 */
@Dao
interface UserDao {

    /**
     * Retrieve live data of a user from database.
     * @param[id] the id of the user.
     * @return LiveData containing the corresponding User
     */
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserLiveData(id: String): LiveData<User?>

    /**
     * Retrieve a user from database.
     * @param[id] the id of the user.
     * @return The corresponding User.
     */
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: String): User?

    /**
     * Add a list of users to the database.
     * @param[users] List of users to add.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    /**
     * Remove all users from database.
     */
    @Query("DELETE FROM user")
    fun clear()

    /**
     * Replace all users with provided list.
     * @param[users] List of users to replace with.
     */
    @Transaction
    fun replace(users: List<User>) {
        clear()
        insert(users)
    }
}
