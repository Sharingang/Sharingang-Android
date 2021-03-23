package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.sharingang.databinding.FragmentEditItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel

class EditItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    private lateinit var existingItem: Item

    private lateinit var binding: FragmentEditItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        existingItem = args.item

        setupBinding()

        binding.editItemButton.setOnClickListener { view: View ->
            viewModel.updateItem(
                existingItem.copy(
                    title = binding.title ?: "",
                    description = binding.description ?: "",
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    category = binding.categorySpinner.selectedItemPosition,
                    categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
                    latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
                    longitude = binding.longitude?.toDoubleOrNull() ?: 0.0
                )
            )
            view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
        }
        return binding.root
    }

    private fun setupBinding() {
        binding.title = existingItem.title
        binding.description = existingItem.description
        binding.price = existingItem.price.toString().format("%.2f")
        binding.categorySpinner.setSelection(existingItem.category)
        binding.latitude = existingItem.latitude.toString()
        binding.longitude = existingItem.longitude.toString()

    }
}