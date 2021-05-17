package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sharingang.databinding.FragmentWishlistViewBinding
import com.example.sharingang.viewmodels.ItemsViewModel
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.viewmodels.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WishlistViewFragment : Fragment() {

    private lateinit var binding: FragmentWishlistViewBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val userViewModel: UserProfileViewModel by activityViewModels()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onResume() {
        super.onResume()
        checkUser()
        userViewModel.refreshListUI(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistViewBinding.inflate(inflater, container, false)
        val adapter = viewModel.setupItemAdapter()
        binding.wishlistview.adapter = adapter
        binding.wishlistview.layoutManager = GridLayoutManager(context, 2)
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.WISHLIST)
        userViewModel.refreshListUI(viewModel)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item ->
                WishlistViewFragmentDirections.actionWishlistViewFragmentToDetailedItemFragment(
                    item
                )
            })
        checkUser()
        return binding.root
    }

    private fun checkUser() {
        if (currentUserProvider.getCurrentUserId() == null) {
            Toast.makeText(this.context, "Login to view your wish list", Toast.LENGTH_SHORT).show()
        }
    }

}
