package com.example.sharingang

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    private lateinit var observer: ImageAccess

    private var imageUri: Uri? = null

    lateinit var binding: FragmentNewItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)
        binding.createItemButton.setOnClickListener { view: View ->
            viewModel.addItem(
                Item(
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    description = binding.description ?: "",
                    title = binding.title ?: "",
                    imageUri = imageUri?.toString()
                )
            )
            view.findNavController().navigate(R.id.action_newItemFragment_to_itemsListFragment)
        }

        observer = ImageAccess(requireActivity().activityResultRegistry) { uri: Uri? ->
            binding.newItemImage.setImageURI(uri)
            imageUri = uri
        }
        lifecycle.addObserver(observer)

        binding.newItemImage.setOnClickListener {
            openGallery()
        }
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
}