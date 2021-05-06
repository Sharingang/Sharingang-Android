package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sharingang.databinding.FragmentItemsListBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.utils.OrderingViewModel

class ItemsListFragment : Fragment() {

    private lateinit var binding: FragmentItemsListBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val orderingViewModel: OrderingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel

        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.ORDERED_ITEMS)
        binding.itemList.layoutManager = GridLayoutManager(context, 2)

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item -> ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment(item) })

        binding.swiperefresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (!it) {
                binding.swiperefresh.isRefreshing = false
                orderItems() // in the case an item is added, need to add redo the ordering
            }
        })
        binding.orderCategorySpinner.setSelection(orderingViewModel.orderByPosition.value!!)
        binding.orderAscendingDescending.setSelection(orderingViewModel.ascendingDescendingPosition.value!!)
        orderItems()
        binding.startOrdering.setOnClickListener { orderItems() }

        return binding.root
    }

    private fun orderItems() {
        orderingViewModel.setOrderByPosition(binding.orderCategorySpinner.selectedItemPosition)
        orderingViewModel.setAscendingDescendingPosition(binding.orderAscendingDescending.selectedItemPosition)
        viewModel.orderItems(
            ItemsViewModel.ORDERING.values()[binding.orderCategorySpinner.selectedItemPosition],
            binding.orderAscendingDescending.selectedItemPosition == 0
        )
    }
}

