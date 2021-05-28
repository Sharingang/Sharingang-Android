package com.example.sharingang.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharingang.databinding.FragmentChatsBinding
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.models.User
import com.example.sharingang.ui.adapters.UserAdapter
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.utils.RecyclerViewDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ChatsFragment takes care of displaying and representing the current
 * chat partners of the logged-in user.
 */
@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private var currentUserId: String? = null
    private lateinit var userAdapter: UserAdapter

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    private val listUsers: MutableList<User> = mutableListOf()

    @Inject
    lateinit var userRepository: UserRepository
    private val usersLiveData: MutableLiveData<List<User>> = MutableLiveData(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserId = currentUserProvider.getCurrentUserId()
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        if (currentUserId != null) {
            userAdapter = UserAdapter(
                requireContext(), listUsers, userRepository, currentUserId!!, lifecycleScope, this
            )
            binding.chatUsersList.adapter = userAdapter
            val decorator = RecyclerViewDecorator()
            decorator.setRecyclerViewDecoration(margin = 10, recyclerView = binding.chatUsersList)
            binding.chatUsersList.layoutManager = LinearLayoutManager(requireContext())
            usersLiveData.observe(viewLifecycleOwner, { newList ->
                (binding.chatUsersList.adapter as UserAdapter).submitList(newList)
            })
        }
        setupUI()
        return binding.root
    }

    /**
     * Sets up the UI (views) with the help of the UserAdapter
     */
    private fun setupUI() {
        listUsers.clear()
        if (currentUserId != null) {
            binding.loggedOutInfo.visibility = View.GONE
            lifecycleScope.launch(Dispatchers.IO) {
                val chatPartners = userRepository.getChatPartners(currentUserId!!)
                chatPartners.forEach {
                    val user = userRepository.get(it)
                    if(!userRepository.hasBeenBlocked(currentUserId!!, by = user!!.id!!) &&
                            !userRepository.hasBeenBlocked(user.id!!, by = currentUserId!!)) {
                        listUsers.add(user)
                    }
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    usersLiveData.postValue(listUsers)
                }
            }
        } else {
            binding.chatUsersList.visibility = View.GONE
            binding.loggedOutInfo.visibility = View.VISIBLE
        }
    }
}
