package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentItemsListBinding
import com.example.sharingang.items.ItemsViewModel

class ItemsListFragment : Fragment() {

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
        viewModel.viewingItem.observe(viewLifecycleOwner, {
            if (it) {
                this.findNavController().navigate(
                    ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment()
                )
                viewModel.onViewItemNavigated()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentItemsListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel
        binding.newItemButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_itemsListFragment_to_newItemFragment)
        }
        binding.goToMap.setOnClickListener { view: View ->
            goToMap(view)
        }
        binding.gotoSearchButton.setOnClickListener { view: View -> goToSearchPage(view) }

        binding.gotoSearchButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_itemsListFragment_to_searchFragment5)
        }

        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter)

        setupNavigation()

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (!it) {
                binding.swiperefresh.isRefreshing = false
            }
        })

        return binding.root
    }

    private fun goToMap(view: View) {
        view.findNavController().navigate(R.id.action_itemsListFragment_to_mapFragment)
    }

    fun goToSearchPage(view: View) {
        view.findNavController().navigate(R.id.action_itemsListFragment_to_searchFragment5)
    }
}

