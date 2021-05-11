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

    override fun getCurrentUserId(): String? = instance.user?.id

    override fun getCurrentUserEmail(): String {
        return if (instance != Instance.LOGGED_OUT) "test-user@example.com" else ""
    }

    override fun getCurrentUserName(): String = instance.user?.name ?: ""

    enum class Instance(val user: User?) {
        LOGGED_OUT(null), FAKE_USER_1(fakeUser1), FAKE_USER_2(fakeUser2)
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
        var instance = Instance.FAKE_USER_1
    }
}
