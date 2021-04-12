package com.example.sharingang

import android.net.Uri
import android.Manifest
import android.location.Location
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
import com.example.sharingang.utils.ImageAccess
import com.example.sharingang.utils.consumeLocation
import com.example.sharingang.utils.doOrGetPermission
import com.example.sharingang.utils.requestPermissionLauncher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

class NewItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    private lateinit var observer: ImageAccess

    private var imageUri: Uri? = null

    lateinit var binding: FragmentNewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = ImageAccess(requireActivity())
        lifecycle.addObserver(observer)
    }

    private lateinit var fusedLocationCreate: FusedLocationProviderClient

    // Allows the cancellation of a location request if, for example, the user exists the activity
    private var cancellationTokenSource = CancellationTokenSource()
    private val requestPermissionLauncher = requestPermissionLauncher(this) {
        doOrGetPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION,
            {
                consumeLocation(
                    fusedLocationCreate,
                    cancellationTokenSource
                ) { updateLocation(it) }
            }, null
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)

        observer.setupImageView(binding.newItemImage)

        bind()
        setupLocationCreate()

        return binding.root
    }

    private fun bind() {
        binding.createItemButton.setOnClickListener { view: View ->
            imageUri = observer.getImageUri()
            viewModel.addItem(Item(
                price = binding.price?.toDoubleOrNull() ?: 0.0,
                description = binding.description ?: "",
                title = binding.title ?: "",
                category = binding.categorySpinner.selectedItemPosition,
                categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
                latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
                longitude = binding.longitude?.toDoubleOrNull() ?: 0.0,
                sold = false,
                imageUri = imageUri?.toString()
            ))
            observer.unregister()
            view.findNavController().navigate(R.id.action_newItemFragment_to_itemsListFragment)
        }
        binding.newItemImage.setOnClickListener {
            observer.openGallery()
        }
        binding.newItemTakePicture.setOnClickListener {
            observer.openCamera()
        }
    }

    private fun setupLocationCreate() {
        fusedLocationCreate = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.newItemGetLocation.setOnClickListener {
            doOrGetPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                {
                    consumeLocation(fusedLocationCreate, cancellationTokenSource) {
                        updateLocation(
                            it
                        )
                    }
                }, requestPermissionLauncher
            )
        }
    }

    private fun updateLocation(location: Location) {
        binding.latitude = location.latitude.toString()
        binding.longitude = location.longitude.toString()
    }
}