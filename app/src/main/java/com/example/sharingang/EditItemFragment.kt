package com.example.sharingang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
    ): View? {
        val binding: FragmentEditItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        item = args.item

        binding.editItemDescription.setText(item.description)

        binding.editItemButton.setOnClickListener { view: View ->
            onEditItemClick(
                view,
                binding.editItemDescription.text.toString()
            )
        }
        return binding.root
    }

    private fun onEditItemClick(view: View, description: String) {
        viewModel.updateItem(item, Item(description))
        view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
    }
}