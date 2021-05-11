package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentSoldItemListBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SoldItemList : Fragment() {


    private lateinit var binding: FragmentSoldItemListBinding
    private val viewModel: ItemsViewModel by activityViewModels()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sold_item_list, container, false)

        setupRecyclerview()

        return binding.root
    }

    private fun setupRecyclerview() {
        val adapter = viewModel.setupItemAdapter()
        binding.soldList.adapter = adapter
        viewModel.getUserSoldItems(currentUserProvider.getCurrentUserId())
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.SOLD_ITEMS)
        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            {
                SoldItemListDirections.actionSoldItemListToDetailedItemFragment(it)
            })
    }

}