package com.example.sharingang

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.sharingang.databinding.FragmentNewItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel

class NewItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    private lateinit var contextActivity: MainActivity

    private var imageUri = MutableLiveData<Uri>()

    private val pickImages =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { it ->
                imageUri.value = it
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentNewItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)

        binding.createItemButton.setOnClickListener { view: View ->
            viewModel.addItem(
                Item(
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    description = binding.description ?: "",
                    title = binding.title ?: "",
                    imageUri = imageUri.value?.toString()
                )
            )
            view.findNavController().navigate(R.id.action_newItemFragment_to_itemsListFragment)
        }
        binding.newItemImage.setOnClickListener {
            openGallery()
        }
        imageUri.observe(viewLifecycleOwner, { uri ->
            binding.newItemImage.setImageURI(uri)
        })
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextActivity = context as MainActivity
    }

    private fun openGallery() {
        contextActivity.checkAndRequestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            "Storage permission is required to add an image from your phone."
        )
        if (contextActivity.permissionGranted) {
            pickImages.launch("image/*")
        }
    }
}