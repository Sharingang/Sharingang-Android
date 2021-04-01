package com.example.sharingang

import android.net.Uri
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

    private lateinit var observer: ImageAccess

    private var imageUri: Uri? = null

    private lateinit var binding: FragmentEditItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = ImageAccess(requireActivity(), ::galleryCallback, ::cameraCallback)
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_item, container, false)

        val args = EditItemFragmentArgs.fromBundle(requireArguments())

        existingItem = args.item

        setupBinding()

        return binding.root
    }

    private fun setupBinding() {
        binding.title = existingItem.title
        binding.description = existingItem.description
        binding.price = existingItem.price.toString().format("%.2f")
        binding.categorySpinner.setSelection(existingItem.category)
        binding.latitude = existingItem.latitude.toString()
        binding.longitude = existingItem.longitude.toString()
        existingItem.imageUri?.let { binding.editItemImage.setImageURI(Uri.parse(it)) }
        binding.editItemImage.setOnClickListener {
            observer.openGallery(requireActivity())
        }
        binding.editItemTakePicture.setOnClickListener {
            cameraUri = observer.openCamera(requireActivity())
        }
        editItemClickListener()
    }

    private fun editItemClickListener() {
        binding.editItemButton.setOnClickListener { view: View ->
            viewModel.updateItem(
                existingItem.copy(
                    title = binding.title ?: "",
                    description = binding.description ?: "",
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    category = binding.categorySpinner.selectedItemPosition,
                    categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
                    latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
                    longitude = binding.longitude?.toDoubleOrNull() ?: 0.0,
                    imageUri = imageUri?.toString()
                )
            )
            observer.unregister()
            view.findNavController().navigate(R.id.action_editItemFragment_to_itemsListFragment)
        }
    }

    private fun galleryCallback(uri: Uri?) {
        uri?.let { imageUri = uri
            binding.editItemImage.setImageURI(uri)
        }
    }

    private fun cameraCallback(res: Boolean?) {
        res?.let {
            if (it) {
                cameraUri?.let { imageUri = cameraUri
                    binding.editItemImage.setImageURI(cameraUri)
                }
            }
        }
    }
}