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

class WishlistViewFragment : Fragment() {

    private lateinit var binding: FragmentWishlistViewBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val userviewModel: UserProfileViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        userviewModel.refreshListUI(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistViewBinding.inflate(inflater, container, false)
        val adapter = viewModel.setupItemAdapter()
        binding.wishlistview.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.WISHLIST)
        userviewModel.refreshListUI(viewModel)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
                {item -> wishlistViewFragmentDirections.actionWishlistViewFragmentToEditItemFragment(item)},
                {item -> wishlistViewFragmentDirections.actionWishlistViewFragmentToDetailedItemFragment(item)})
//                {item ->  })

        return binding.root
    }

}