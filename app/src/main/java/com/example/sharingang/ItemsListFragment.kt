package com.example.sharingang

import android.content.Context
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

class ItemsListFragment : Fragment() {

    private lateinit var binding: FragmentItemsListBinding
    private val viewModel: ItemsViewModel by activityViewModels()

    private fun setupNavigation() {
        viewModel.navigateToEditItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                this.findNavController().navigate(
                    ItemsListFragmentDirections.actionItemsListFragmentToEditItemFragment(item)
                )
                viewModel.onEditItemNavigated()
            }
        })
        viewModel.navigateToDetailItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                this.findNavController().navigate(
                    ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment(item)
                )
                viewModel.onViewItemNavigated()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel

        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.ALL_ITEMS)

        setupNavigation()

        binding.swiperefresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (!it) {
                binding.swiperefresh.isRefreshing = false
            }
        })

        return binding.root
    }
}

