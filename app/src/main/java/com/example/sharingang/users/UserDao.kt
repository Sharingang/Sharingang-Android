package com.example.sharingang.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
}
