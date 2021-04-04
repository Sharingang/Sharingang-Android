package com.example.sharingang

import android.content.Context
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
        viewModel.viewingItem.observe(viewLifecycleOwner, {
            if (it) {
                this.findNavController().navigate(
                        ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment()
                )
                viewModel.onViewItemNavigated()
            }
        })
    }

    fun setupButtons() {
        binding.newItemButton.setOnClickListener { view: View -> goToNewItem(view) }
        binding.goToMap.setOnClickListener { view: View ->
            goToMap(view)
        }
        binding.userProfileButton.setOnClickListener {
            val sharedPreferences = activity?.getSharedPreferences(
                getString(R.string.preference_user_info),
                Context.MODE_PRIVATE
            )
            val action =
                if (sharedPreferences == null) {
                    ItemsListFragmentDirections.actionItemsListFragmentToUserProfileFragment("test")
                } else {
                    ItemsListFragmentDirections.actionItemsListFragmentToUserProfileFragment(
                        sharedPreferences.getString(
                            getString(R.string.account_firebase_uid), "test"
                        )!!
                    )
                }
            it.findNavController().navigate(action)
        }
        binding.gotoSearchButton.setOnClickListener { view: View -> goToSearchPage(view) }
        binding.gotoAccount.setOnClickListener { view: View -> goToAccount(view) }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel

        setupButtons()

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

    private fun goToNewItem(view: View){
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToNewItemFragment()
        )
    }

    private fun goToMap(view: View) {
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToMapFragment()
        )
    }

    fun goToSearchPage(view: View) {
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToSearchFragment()
        )
    }

    fun goToAccount(view: View) {
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToAccountFragment()
        )
    }
}

