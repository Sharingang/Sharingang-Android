package com.example.sharingang.users

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserLiveData(id: String): LiveData<User?>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    @Query("DELETE FROM user")
    fun clear()

    @Transaction
    fun replace(users: List<User>) {
        clear()
        insert(users)
    }
}
