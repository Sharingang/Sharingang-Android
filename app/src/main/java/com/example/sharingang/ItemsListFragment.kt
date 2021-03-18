package com.example.sharingang

import android.content.Intent
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
import com.example.sharingang.items.ItemListener
import com.example.sharingang.items.ItemsAdapter
import com.example.sharingang.items.ItemsViewModel

class ItemsListFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    val adapter = ItemsAdapter(ItemListener { item -> viewModel.onEditItemClicked(item) })

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
        binding.itemList.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        viewModel.navigateToEditItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                this.findNavController().navigate(ItemsListFragmentDirections.actionItemsListFragmentToEditItemFragment(item))
                viewModel.onEditItemNavigated()
            }
        })
        binding.goToMap.setOnClickListener {
            startActivity(Intent(this.activity, MapActivity::class.java))
        }
        binding.gotoSearchButton.setOnClickListener{
            view -> view.findNavController().navigate(R.id.action_itemsListFragment_to_searchFragment);
        }
        return binding.root
    }


}