package com.example.sharingang

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>()
    val user: LiveData<User?> =
        Transformations.switchMap(_userId) { id ->
            if (id != null) {
                userRepository.user(id)
            } else {
                null
            }
        }

    fun setUser(userId: String) {
        _userId.postValue(userId)
    }
    /*
    // Only for testing
    suspend fun createTestUser(userId: String) {
        if (userRepository.get(userId) == null) {
            userRepository.add(
                User(
                    id = userId,
                    name = "Test user",
                    profilePicture = "https://picsum.photos/200"
                )
            )
        }
    }
    */

}
