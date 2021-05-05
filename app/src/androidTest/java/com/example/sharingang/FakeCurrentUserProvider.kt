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
            userRepository.add(fakeUser1)
            userRepository.add(fakeUser2)
        }
    }

    override fun getCurrentUserId(): String? {
        return if (instance == 1) fakeUser1.id else fakeUser2.id
    }

    override fun getCurrentUserEmail(): String {
        return "test-user@example.com"
    }

    override fun getCurrentUserName(): String {
        return if (instance == 1) fakeUser1.name else fakeUser2.name
    }

    companion object {
        val fakeUser1 = User(
            id = "fakeUserID1",
            name = "Test User",
            profilePicture = "https://picsum.photos/200"
        )

        val fakeUser2 = User(
            id = "fakeUserID2",
            name = "Test User 2",
            profilePicture = "https://picsum.photos/200"
        )
        var instance = 1
    }
}
