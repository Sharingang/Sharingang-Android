package com.example.sharingang

import androidx.lifecycle.*
import com.example.sharingang.items.Item
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>()

    private val _wishlistContains : MutableLiveData<Boolean> = MutableLiveData(false)

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

    fun wishlistContains(item: Item): Boolean {
        viewModelScope.launch(Dispatchers.IO){
            if(_userId.value != null){
                val user : User? = userRepository.get(_userId.value!!)
                if(user != null){
                    _wishlistContains.postValue(user.wishlist.value!!.contains(item.id))
                }
            }
        }
        return _wishlistContains.value!!
    }

    fun modifyWishList(item: Item?, add: Boolean){
        if(item != null){
            viewModelScope.launch(Dispatchers.IO) {
                val user : User? = userRepository.get(_userId.value!!)
                if(user != null){
                    val newList = user.wishlist.value!!
                    val res =
                        if(add) newList.add(item.id!!)
                        else newList.remove(item.id!!)
                    user.wishlist.postValue(newList)
                }
            }
        }
    }

}

