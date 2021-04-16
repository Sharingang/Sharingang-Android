package com.example.sharingang

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.sharingang.items.Item
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>()

    private val _wishlistContains : MutableLiveData<Boolean> = MutableLiveData(false)
    val wishlistContains : LiveData<Boolean>
        get() = _wishlistContains

    val user: LiveData<User?> =
        Transformations.switchMap(_userId) { id ->
            if (id != null) {
                userRepository.user(id)
            } else {
                null
            }
        }

    fun setUser(userId: String?) {
        _userId.postValue(userId)
    }

    fun wishlistContains(item: Item?, userId: String?){
        if(item != null && userId != null){
            viewModelScope.launch(Dispatchers.IO) {
                _wishlistContains.postValue(userRepository.get(userId)!!.wishlist.contains(item.id!!))
            }
        }
    }


    fun modifyWishList(item: Item?, userId: String?) {
        if(item != null){
            viewModelScope.launch(Dispatchers.IO) {
                if(userId == null) return@launch
                val user : User? = userRepository.get(userId)

                val currentList = ArrayList(user!!.wishlist.split(" , "))
                val add = user.wishlist.contains(item.id!!)
                _wishlistContains.postValue(!add)
                if(!add){
                    currentList.add(item.id)
                } else {
                    currentList.remove(item.id)
                }
                user.wishlist = currentList.joinToString(separator = " , ")
                userRepository.update(user)

            }
        }
    }

}

