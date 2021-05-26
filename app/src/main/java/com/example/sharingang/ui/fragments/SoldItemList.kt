package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharingang.R
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.databinding.FragmentSoldItemListBinding
import com.example.sharingang.viewmodels.ItemsViewModel
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
        val userId = currentUserProvider.getCurrentUserId()
        binding.soldList.adapter = adapter
        listOf(binding.soldButton, binding.boughtButton).forEach {
            it.setOnClickListener {
                viewModel.updateSoldBought(userId, binding.soldButton.isChecked)
            }
        }
        viewModel.updateSoldBought(userId, binding.soldButton.isChecked)
        viewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.USER_SOLD_BOUGHT)
        viewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            {
                SoldItemListDirections.actionSoldItemListToDetailedItemFragment(it)
            })
    }
}
