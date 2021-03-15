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
    private lateinit var item: Item

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentEditItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        item = args.item

        binding.title = item.title
        binding.description = item.description
        binding.price = item.price.toString().format("%.2f")

        binding.editItemButton.setOnClickListener { view: View ->
            viewModel.updateItem(
                item,
                Item(title = binding.title ?: "", description = binding.description ?: "", price = binding.price?.toDoubleOrNull() ?: 0.0)

            )
            view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
        }
        return binding.root
    }
}