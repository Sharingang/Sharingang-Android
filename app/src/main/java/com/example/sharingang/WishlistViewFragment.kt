package com.example.sharingang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentWishlistViewBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
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
        userViewModel.refreshListUI(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistViewBinding.inflate(inflater, container, false)
        val adapter = viewModel.setupItemAdapter(currentUserProvider.getCurrentUserId())
        binding.wishlistview.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.WISHLIST)
        userViewModel.refreshListUI(viewModel)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
                {item -> WishlistViewFragmentDirections.actionWishlistViewFragmentToNewEditFragment(item)},
                {item -> WishlistViewFragmentDirections.actionWishlistViewFragmentToDetailedItemFragment(item)})

        return binding.root
    }

}