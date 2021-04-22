package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentItemsListBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemsListFragment : Fragment() {

    private lateinit var binding: FragmentItemsListBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel

        val adapter = viewModel.setupItemAdapter(currentUserProvider.getCurrentUserId())
        binding.itemList.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.ALL_ITEMS)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
                {item -> ItemsListFragmentDirections.actionItemsListFragmentToEditItemFragment(item)},
                {item -> ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment(item)})

        binding.swiperefresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (!it) {
                binding.swiperefresh.isRefreshing = false
            }
        })

        return binding.root
    }
}

