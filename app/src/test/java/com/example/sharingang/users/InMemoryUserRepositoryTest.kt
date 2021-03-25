package com.example.sharingang.users

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.example.sharingang.CacheDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.util.*

class InMemoryUserRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun canAddUser() {
        val repo: UserRepository = InMemoryUserRepository()
        runBlocking {
            val user = generateSampleUser()
            assert(repo.addUser(user))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun canAddUserThrowsWhenIdNull() {
        val repo: UserRepository = InMemoryUserRepository()
        runBlocking {
            val user = generateSampleUser().copy(id = null)
            repo.addUser(user)
        }
    }

    @Test
    fun canGetAddedUser() {
        val repo: UserRepository = InMemoryUserRepository()
        runBlocking {
            val user = generateSampleUser()

            repo.addUser(user)

            val retrievedUser = repo.getUser(user.id!!)

            assert(retrievedUser == user)
        }
    }

    @Test
    fun canUpdateUser() {
        val repo: UserRepository = InMemoryUserRepository()
        runBlocking {
            val user = generateSampleUser()
            repo.addUser(user)

            val updatedUser = user.copy(name = "updated name")
            assert(repo.updateUser(updatedUser))

            assert(repo.getUser(updatedUser.id!!) == updatedUser)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateUserThrowsExceptionWhenIdNull() {
        val repo: UserRepository = InMemoryUserRepository()
        runBlocking {
            // New User with no id
            val user = generateSampleUser().copy(id = null)
            repo.updateUser(user)
        }
    }

    private fun generateSampleUser(index: Int = 0): User {
        return User(
                id = UUID.randomUUID().toString(),
                name = "Name $index",
                profilePicture = "https://example.com/image.jpg"
        )
    }
}
