package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.databinding.FragmentBlockedUsersBinding
import com.example.sharingang.models.User
import com.example.sharingang.ui.adapters.BlockedUserAdapter
import com.example.sharingang.utils.RecyclerViewDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BlockedUsersFragment : Fragment() {

    private lateinit var blockedUserAdapter: BlockedUserAdapter
    private lateinit var binding: FragmentBlockedUsersBinding
    private var currentUserId: String? = null

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

    private val listUsers: MutableList<User> = mutableListOf()
    private val blockedUsersLiveData: MutableLiveData<List<User>> = MutableLiveData(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserId = currentUserProvider.getCurrentUserId()
        binding = FragmentBlockedUsersBinding.inflate(inflater, container, false)
        if (currentUserId != null) {
            blockedUserAdapter = BlockedUserAdapter(
                requireContext(), listUsers, currentUserId!!, userRepository, lifecycleScope
            )
            binding.blockedUsersList.adapter = blockedUserAdapter
            val decorator = RecyclerViewDecorator()
            decorator.setRecyclerViewDecoration(margin = 10, binding.blockedUsersList)
            binding.blockedUsersList.layoutManager = LinearLayoutManager(requireContext())
            blockedUsersLiveData.observe(viewLifecycleOwner, { newList ->
                (binding.blockedUsersList.adapter as BlockedUserAdapter).submitList(newList)
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
            lifecycleScope.launch(Dispatchers.IO) {
                val blockedUsers = userRepository.getBlockedUsers(currentUserId!!)
                blockedUsers.forEach {
                    val user = userRepository.get(it)
                    listUsers.add(user!!)
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    blockedUsersLiveData.postValue(listUsers)
                }
            }
        }
    }
}
