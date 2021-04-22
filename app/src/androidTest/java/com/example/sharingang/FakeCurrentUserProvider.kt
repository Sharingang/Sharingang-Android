package com.example.sharingang

import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FakeCurrentUserProvider @Inject constructor(
    private val userRepository: UserRepository,
) : CurrentUserProvider {

    init {
        runBlocking(Dispatchers.IO) {
            userRepository.add(fakeUser)
        }
    }

    override fun getCurrentUserId(): String? {
        return fakeUser.id
    }

    override fun getCurrentUserEmail(): String {
        return "test-user@example.com"
    }

    override fun getCurrentUserName(): String? {
        return "Test User"
    }

    companion object {
        val fakeUser = User(
            id = "fakeUserID",
            name = "Test User",
            profilePicture = "https://picsum.photos/200",
        )
    }
}
