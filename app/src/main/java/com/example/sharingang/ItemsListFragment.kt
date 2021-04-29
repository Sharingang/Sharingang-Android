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
import com.example.sharingang.utils.UIViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemsListFragment : Fragment() {

    private lateinit var binding: FragmentItemsListBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val uiViewModel: UIViewModel by activityViewModels()
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
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.ORDERED_ITEMS)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
                {item -> ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment(item)})

        binding.swiperefresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (!it) {
                binding.swiperefresh.isRefreshing = false
                orderItems() // in the case an item is added, need to add redo the ordering
            }
        })
        binding.orderCategorySpinner.setSelection(uiViewModel.orderByPosition.value!!)
        binding.orderAscendingDescending.setSelection(uiViewModel.ascendingDescendingPosition.value!!)
        if(uiViewModel.orderBy()){
            orderItems()
        }
        binding.startOrdering.setOnClickListener { orderItems() }

        return binding.root
    }

    private fun orderItems() {
        uiViewModel.setOrderByPosition(binding.orderCategorySpinner.selectedItemPosition)
        uiViewModel.setAscendingDescendingPosition(binding.orderAscendingDescending.selectedItemPosition)
        viewModel.orderItems(
            ItemsViewModel.ORDERING.values()[binding.orderCategorySpinner.selectedItemPosition],
            binding.orderAscendingDescending.selectedItemPosition == 0
        )
    }
}

