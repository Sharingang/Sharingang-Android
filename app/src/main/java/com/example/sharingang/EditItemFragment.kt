package com.example.sharingang

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var observer : ImageAccess

    private var imageUri: Uri? = null

    lateinit var binding: FragmentEditItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        observer = ImageAccess(requireActivity().activityResultRegistry) { uri: Uri? ->
            binding.editItemImage.setImageURI(uri)
            imageUri = uri
        }
        lifecycle.addObserver(observer)

        existingItem = args.item

        bind(binding, existingItem)

        return binding.root
    }

    private fun openGallery() {
        observer.checkAndRequestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "Storage permission is required to add an image from your phone.",
            requireActivity()
        )
        if (observer.storagePermissionGranted) {
            observer.openGallery()
        }
    }

    private fun bind(binding: FragmentEditItemBinding, item: Item) {
        binding.title = existingItem.title
        binding.description = existingItem.description
        binding.price = existingItem.price.toString().format("%.2f")
        existingItem.imageUri?.let {
            binding.editItemImage.setImageURI(Uri.parse(it))
        }
        binding.editItemImage.setOnClickListener {
            openGallery()
        }
        binding.editItemButton.setOnClickListener { view: View ->
            viewModel.updateItem(
                existingItem.copy(
                    title = binding.title ?: "",
                    description = binding.description ?: "",
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    imageUri = imageUri?.toString()
                )
            )
            view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
        }
    }
}