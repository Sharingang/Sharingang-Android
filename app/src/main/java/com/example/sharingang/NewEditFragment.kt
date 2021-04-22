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
import com.example.sharingang.databinding.FragmentNewEditItemBinding
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
class NewEditFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    private var existingItem: Item? = null

    private lateinit var observer: ImageAccess

    private var imageUri: Uri? = null

    private lateinit var binding: FragmentNewEditItemBinding

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_edit_item, container, false)
        setupNewOrEditFragment()
        setupAutocomplete()
        userId = currentUserProvider.getCurrentUserId()

        observer.setupImageView(binding.itemImage)
        setupBinding()

        setupButtonActions()
        setupLocation()
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

    private fun setupButtonActions() {
        listOf(binding.createItemButton, binding.editItemButton).forEach {
            it.setOnClickListener { view: View ->
                imageUri = observer.getImageUri()
                val item = itemToAdd()
                if (existingItem == null) {
                    viewModel.addItem(item)
                } else {
                    viewModel.updateItem(item)
                }
                observer.unregister()
                view.findNavController().navigate(R.id.action_newEditFragment_to_itemsListFragment)
            }
        }
        binding.itemImage.setOnClickListener {
            observer.openGallery()
        }
        binding.itemTakePicture.setOnClickListener {
            observer.openCamera()
        }
    }

    private fun itemToAdd(): Item {
        return Item(
            id = existingItem?.id,
            title = binding.title ?: "",
            description = binding.description ?: "",
            //image = existingItem?.image ?: "",
            imageUri = imageUri?.toString() ?: existingItem?.imageUri,
            price = binding.price?.toDoubleOrNull() ?: 0.0,
            sold = existingItem?.sold ?: false,
            category = binding.categorySpinner.selectedItemPosition,
            categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
            latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
            longitude = binding.longitude?.toDoubleOrNull() ?: 0.0,
            userId = existingItem?.userId ?: userId,
            createdAt = existingItem?.createdAt,
            localId = existingItem?.localId ?: 0
        )
    }

    private fun setupLocation() {
        fusedLocationCreate = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.itemGetLocation.setOnClickListener {
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
        updateLocationWithCoordinates(location.latitude, location.longitude)
    }

    private fun updateLocationWithCoordinates(latitude: Double, longitude: Double) {
        binding.latitude = latitude.toString()
        binding.longitude = longitude.toString()
        val address =
            geocoder.getFromLocation(latitude, longitude, 1).getOrNull(0)
        binding.postalAddress.text = address?.getAddressLine(0) ?: ""
    }

    private fun setupNewOrEditFragment() {
        val args = NewEditFragmentArgs.fromBundle(requireArguments())
        existingItem = args.item
        listOf(binding.editItemPrompt,binding.editItemButton).forEach {
            it.visibility = if(existingItem==null) View.GONE else View.VISIBLE
        }
        listOf(binding.newItemPrompt,binding.createItemButton).forEach{
            it.visibility = if(existingItem==null) View.VISIBLE else View.GONE
        }
    }

    private fun setupBinding() {
        existingItem?.let {
            binding.title = it.title
            binding.description = it.description
            binding.price = it.price.toString().format("%.2f")
            binding.categorySpinner.setSelection(it.category)
            binding.latitude = it.latitude.toString()
            binding.longitude = it.longitude.toString()
            it.imageUri?.let { uri ->
                binding.itemImage.setImageURI(Uri.parse(uri))
            }
            updateLocationWithCoordinates(it.latitude, it.longitude)
        }
    }
}