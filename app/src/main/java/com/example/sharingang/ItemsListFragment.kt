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
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

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
            val action =
                    ItemsListFragmentDirections.actionItemsListFragmentToUserProfileFragment("test")
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

        handleDeepLink()

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

    private fun goToSearchPage(view: View) {
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToSearchFragment()
        )
    }

    private fun goToAccount(view: View) {
        view.findNavController().navigate(
                ItemsListFragmentDirections.actionItemsListFragmentToAccountFragment()
        )
    }

    private fun handleDeepLink() {
        val activity = this.activity ?: return

        Firebase.dynamicLinks
            .getDynamicLink(activity.intent)
            .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->
                val deepLink = pendingDynamicLinkData?.link
                when (deepLink?.path) {
                    "/item" -> {
                        deepLink.getQueryParameter("id")?.let {
                            viewModel.goToItemDetail(it)
                        }
                    }
                }
            }
    }
}

