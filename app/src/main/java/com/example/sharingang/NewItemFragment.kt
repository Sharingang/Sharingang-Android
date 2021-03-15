package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.sharingang.databinding.FragmentNewItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel

class NewItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentNewItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)

        binding.createItemButton.setOnClickListener { view: View ->
            viewModel.addItem(
                Item(
                    description = binding.description ?: "",
                    title = binding.title ?: "",
                    price = binding.price?.toDoubleOrNull() ?: 0.0

                )
            )
            view.findNavController().navigate(R.id.action_newItemFragment_to_itemsListFragment)
        }
        return binding.root
    }
}