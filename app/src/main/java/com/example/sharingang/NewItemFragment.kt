package com.example.sharingang

import android.Manifest
import android.location.Geocoder
import android.location.Location
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
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.utils.ImageAccess
import com.example.sharingang.utils.consumeLocation
import com.example.sharingang.utils.doOrGetPermission
import com.example.sharingang.utils.requestPermissionLauncher
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()

    private lateinit var observer: ImageAccess

    private var imageUri: Uri? = null

    lateinit var binding: FragmentNewItemBinding

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    private var userId: String? = null

    private lateinit var fusedLocationCreate: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = ImageAccess(requireActivity())
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false)

        userId = currentUserProvider.getCurrentUserId()

        observer.setupImageView(binding.newItemImage)

        bind()
        setupLocationCreate()
        setupAutocomplete()
        return binding.root
    }

    private fun setupAutocomplete() {
        geocoder = Geocoder(requireContext())
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_api_key))
        }
        val autocompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteSupportFragment.setHint(getString(R.string.enter_address))
        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS)
        autocompleteSupportFragment.setPlaceFields(listOf(Place.Field.ADDRESS))
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                binding.postalAddress.text = place.address
                val address = geocoder.getFromLocationName(place.address, 1).getOrNull(0)
                binding.latitude = address?.latitude.toString()
                binding.longitude = address?.longitude.toString()
            }

            override fun onError(status: Status) {
                Log.e("Error", "$status")
            }
        })
    }

    private fun bind() {
        binding.createItemButton.setOnClickListener { view: View ->
            imageUri = observer.getImageUri()
            viewModel.addItem(
                Item(
                    price = binding.price?.toDoubleOrNull() ?: 0.0,
                    description = binding.description ?: "",
                    title = binding.title ?: "",
                    category = binding.categorySpinner.selectedItemPosition,
                    categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
                    latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
                    longitude = binding.longitude?.toDoubleOrNull() ?: 0.0,
                    sold = false,
                    imageUri = imageUri?.toString(),
                    userId = userId
                )
            )
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
        val address =
            geocoder.getFromLocation(location.latitude, location.longitude, 1).getOrNull(0)
        binding.postalAddress.text = address?.getAddressLine(0)
    }
}