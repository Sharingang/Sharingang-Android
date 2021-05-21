package com.example.sharingang.ui.fragments

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.databinding.FragmentNewEditItemBinding
import com.example.sharingang.models.Item
import com.example.sharingang.utils.ImageAccess
import com.example.sharingang.utils.consumeLocation
import com.example.sharingang.utils.doOrGetPermission
import com.example.sharingang.utils.requestPermissionLauncher
import com.example.sharingang.viewmodels.ItemsViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment to edit and create items.
 */
@AndroidEntryPoint
class NewEditFragment : Fragment() {

    private val args: NewEditFragmentArgs by navArgs()
    private val viewModel: ItemsViewModel by activityViewModels()

    private var existingItem: Item? = null
    private var userId: String? = null

    private lateinit var imageAccess: ImageAccess

    private var imageUri: Uri? = null

    private lateinit var binding: FragmentNewEditItemBinding

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

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
        imageAccess = ImageAccess(requireActivity())
        lifecycle.addObserver(imageAccess)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userId = currentUserProvider.getCurrentUserId()
        existingItem = args.item

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_edit_item, container, false)

        setupAddressAutocomplete()
        setupLocation()
        setupImagePicker()
        setupItemForm()
        setupSaveButton()

        return binding.root
    }

    private fun setupAddressAutocomplete() {
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

    private fun setupImagePicker() {
        imageAccess.setupImageView(binding.itemImage)
        binding.itemOpenGallery.setOnClickListener {
            imageAccess.openGallery()
        }
        binding.itemTakePicture.setOnClickListener {
            imageAccess.openCamera()
        }
    }

    /**
     * Setup click listener for the save button
     */
    private fun setupSaveButton() {
        val button = binding.saveItemButton
        button.setOnClickListener {
            if (validateForm()) {
                saveItem()
            }
        }
    }

    /**
     * Save the item to the database and navigate to it
     * Doesn't perform validation!
     */
    private fun saveItem() {
        val button = binding.saveItemButton
        button.isClickable = false
        binding.isLoading = true
        imageUri = imageAccess.getImageUri()
        val item = itemToAdd()
        viewModel.setItem(item) { itemId ->
            binding.isLoading = false
            if (itemId != null) {
                Snackbar.make(binding.root, getString(R.string.item_save_success), Snackbar.LENGTH_SHORT).show()
                imageAccess.unregister()
                if (existingItem == null) {
                    button.findNavController().navigate(NewEditFragmentDirections.actionNewEditFragmentToItemsListFragment())
                } else {
                    button.findNavController().navigate(NewEditFragmentDirections.actionNewEditFragmentToDetailedItemFragment(item))
                }
            } else {
                button.isClickable = true
                Snackbar.make(binding.root, getString(R.string.item_save_failure), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Check if the form is valid.
     * If not, it displays error messages on the view.
     *
     * @return whether the form is valid and ready to be saved
     */
    private fun validateForm(): Boolean {
        val titleEmpty = binding.itemTitle.text?.trim()?.isEmpty() ?: true
        binding.itemTitleContainer.error = if (titleEmpty) {
            binding.itemTitle.requestFocus()
            getString(R.string.required_field)
        } else null
        val discountLowerPrice = if (binding.switchIsDiscount.isChecked){
            binding.priceDiscount?.toDouble() ?: 0.0 < binding.price?.toDouble() ?: 0.0
        } else true
        binding.itemDiscountContainer.error = if (!discountLowerPrice) {
            binding.discountPrice.requestFocus()
            getString(R.string.discount_lower)
        } else null

        return !titleEmpty && discountLowerPrice
    }

    private fun itemToAdd(): Item {
        return Item(
            id = existingItem?.id,
            title = binding.title ?: "",
            description = binding.description ?: "",
            image = imageUri?.toString() ?: existingItem?.image,
            price = binding.price?.toDoubleOrNull() ?: 0.0,
            quantity = binding.itemQuantity.text.toString().toIntOrNull() ?: 1,
            reviews = mapOf(currentUserProvider.getCurrentUserId()!! to false),
            sold = existingItem?.sold ?: false,
            category = binding.categorySpinner.selectedItemPosition,
            categoryString = resources.getStringArray(R.array.categories)[binding.categorySpinner.selectedItemPosition],
            latitude = binding.latitude?.toDoubleOrNull() ?: 0.0,
            longitude = binding.longitude?.toDoubleOrNull() ?: 0.0,
            userId = existingItem?.userId ?: userId!!, // The form is only displayed for logged in users
            createdAt = existingItem?.createdAt,
            localId = existingItem?.localId ?: 0,
            request = binding.switchIsRequest.isChecked,
            discount = binding.switchIsDiscount.isChecked,
            discountPrice = binding.priceDiscount?.toDoubleOrNull() ?: 0.0
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

    /**
     * Retrieve the existing item and populate the bindings
     * Add event listener to validate the form
     */
    private fun setupItemForm() {
        binding.isNewItem = existingItem == null
        binding.isAuthenticated = userId != null

        existingItem?.let {
            binding.title = it.title
            binding.description = it.description
            binding.price = it.price.toString().format("%.2f")
            binding.categorySpinner.setSelection(it.category)
            binding.latitude = it.latitude.toString()
            binding.longitude = it.longitude.toString()
            it.image?.let { url ->
                Glide.with(requireContext()).load(url).into(binding.itemImage)
            }
            binding.switchIsRequest.isChecked = it.request
            binding.switchIsDiscount.isChecked = it.discount
            binding.isDiscount = it.discount
            setupDiscountSwitch()
            binding.priceDiscount = it.discountPrice.toString().format("%.2f")
            updateLocationWithCoordinates(it.latitude, it.longitude)
        }
    }

    private fun setupDiscountSwitch() {
        binding.switchIsDiscount.setOnCheckedChangeListener{_, isChecked ->
            binding.isDiscount = isChecked
        }
    }
}
