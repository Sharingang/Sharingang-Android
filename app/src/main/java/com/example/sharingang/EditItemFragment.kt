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
import com.example.sharingang.databinding.FragmentEditItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel

class EditItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    private lateinit var contextActivity: MainActivity
    private lateinit var existingItem: Item

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
        val binding: FragmentEditItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        existingItem = args.item

        bind(binding, existingItem)

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
                    imageUri = imageUri.value.toString()
                )
            )
            view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
        }
        imageUri.observe(viewLifecycleOwner, { uri ->
            binding.editItemImage.setImageURI(uri)
        })
    }
}