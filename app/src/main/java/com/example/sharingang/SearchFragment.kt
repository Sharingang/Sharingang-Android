package com.example.sharingang

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: ItemsViewModel by activityViewModels()
    private val userViewModel: UserProfileViewModel by activityViewModels()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    private lateinit var binding: FragmentSearchBinding
    private var contained: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val adapter = viewModel.setupItemAdapter()
        val userId = currentUserProvider.getCurrentUserId()
        if (userId != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                isSubscribed(userId)
            }
        }
        binding.itemSearchList.adapter = adapter
        binding.itemSearchList.layoutManager = GridLayoutManager(context, 2)
        viewModel.addObserver(
            viewLifecycleOwner,
            adapter,
            ItemsViewModel.OBSERVABLES.SEARCH_RESULTS
        )
        binding.sflSearchButton.setOnClickListener {
            viewModel.searchItems(
                binding.searchText.text.toString(),
                binding.searchCategorySpinner.selectedItemPosition
            )
        }
        binding.searchCategorySpinner
        userViewModel.subscriptionsContains.observe(viewLifecycleOwner, { contained = it })
        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item -> SearchFragmentDirections.actionSearchFragmentToDetailedItemFragment(item) })
        clearSearch()
        binding.clearSearchButton.setOnClickListener { clearSearch() }

        return binding.root
    }

    private fun clearSearch() {
        viewModel.clearSearchResults()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val subscribe = menu.findItem(R.id.menuSubscribe)
        val unsubscribe = menu.findItem(R.id.menuUnsubscribe)
        val userId = currentUserProvider.getCurrentUserId()
        if (userId != null) {
            unsubscribe.isVisible = contained
            subscribe.isVisible = !contained
        } else {
            unsubscribe.isVisible = false
            subscribe.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSubscribe, R.id.menuUnsubscribe -> {
                updateSubscriptions()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun isSubscribed(userId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            userViewModel.subscriptionContains(
                userId,
                binding.searchCategorySpinner.selectedItem.toString()
            )
            activity?.invalidateOptionsMenu()
        }
    }

    private fun updateSubscriptions() {
        val category = binding.searchCategorySpinner.selectedItem.toString()
        val userId = currentUserProvider.getCurrentUserId()!!
        lifecycleScope.launch(Dispatchers.IO) {
            userViewModel.subscriptionSet(userId, category)
            isSubscribed(userId)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val userId = currentUserProvider.getCurrentUserId()
        if (userId != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                isSubscribed(userId)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Nothing
    }

}