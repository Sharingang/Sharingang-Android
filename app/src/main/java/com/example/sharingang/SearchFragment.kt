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
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.ItemsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSearchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val adapter = viewModel.setupItemAdapter()
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

        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item -> SearchFragmentDirections.actionSearchFragmentToDetailedItemFragment(item) })
        clearSearch()
        binding.clearSearchButton.setOnClickListener { clearSearch() }

        return binding.root
    }

    private fun clearSearch() {
        viewModel.clearSearchResults()
    }

}
