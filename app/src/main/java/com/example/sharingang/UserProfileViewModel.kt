package com.example.sharingang

import androidx.lifecycle.*
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemRepository
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    currentUserProvider: CurrentUserProvider,
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>(currentUserProvider.getCurrentUserId())
    val userId: LiveData<String?>
        get() = _userId

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

    fun refreshListUI(viewModel: ItemsViewModel){
        viewModelScope.launch(Dispatchers.IO){
            if(userId.value!= null){
                val itemList = ArrayList<Item>()
                for(str in userRepository.get(userId.value!!)!!.wishlist){
                    val item = itemRepository.get(str)
                    if(item != null){
                        itemList.add(item)
                    }
                }
                viewModel.setWishList(itemList)
            }
        }
    }

    fun setUser(userId: String?) {
        _userId.postValue(userId)
    }


    fun wishlistContains(item: Item?){
        if(item != null){
            viewModelScope.launch(Dispatchers.IO) {
                _wishlistContains.postValue(userRepository.get(userId.value!!)!!.wishlist.contains(item.id!!))
            }
        }
    }


    fun modifyWishList(item: Item?) {
        if(item != null){
            viewModelScope.launch(Dispatchers.IO) {
                val user : User? = userRepository.get(userId.value!!)

                val currentList = ArrayList(user!!.wishlist)
                val add = user.wishlist.contains(item.id!!)
                _wishlistContains.postValue(!add)
                if(!add){
                    currentList.add(item.id)
                } else {
                    currentList.remove(item.id)
                }
                user.wishlist = currentList
                userRepository.update(user)
            }
        }
    }

}

