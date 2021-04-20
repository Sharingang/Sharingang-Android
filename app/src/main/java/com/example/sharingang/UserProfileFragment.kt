package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val viewModel: UserProfileViewModel by viewModels()
    private val itemsViewModel: ItemsViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileFragmentBinding.inflate(inflater, container, false)

        // If no userId is provided, we get the user that is currently logged in.
        val userId = when(args.userId) {
                null, "" -> currentUserProvider.getCurrentUserId()
                else -> args.userId
        }
        viewModel.setUser(userId)

        setupRecyclerView(userId)

        viewModel.user.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                binding.nameText.text = user.name
                Glide.with(this).load(user.profilePicture).into(binding.imageView)
            }
        })

        binding.viewModel = viewModel

        return binding.root
    }

    private fun setupRecyclerView(userId: String?) {
        val adapter = itemsViewModel.setupItemAdapter()
        binding.userItemList.adapter = adapter
        itemsViewModel.getUserItem(userId)
        itemsViewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.USER_ITEMS)

        itemsViewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            {item -> UserProfileFragmentDirections.actionUserProfileFragmentToEditItemFragment(item)},
            {item -> UserProfileFragmentDirections.actionUserProfileFragmentToDetailedItemFragment(item)})
    }

}
