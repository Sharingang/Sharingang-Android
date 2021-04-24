package com.example.sharingang


import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ImageAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val userViewModel: UserProfileViewModel by viewModels()
    private val itemsViewModel: ItemsViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding

    // This is the currently logged in user
    private var currentUserId: String? = null
    private lateinit var imageAccess: ImageAccess

    // This is the user whose profile is shown (can be different from currentUserId)
    private var shownUserProfileId: String? = null
    private var loggedInUserEmail: String? = null
    private var imageUri: Uri? = null

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileFragmentBinding.inflate(inflater, container, false)
        currentUserId = currentUserProvider.getCurrentUserId()
        // If no userId is provided, we get the user that is currently logged in.
        shownUserProfileId = when (args.userId) {
            null, "" -> currentUserId
            else -> args.userId
        }
        userViewModel.setUser(shownUserProfileId)
        imageAccess = ImageAccess(requireActivity())
        imageAccess.setupImageView(binding.imageView)
        lifecycle.addObserver(imageAccess)
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            displayUserFields(user)
        })
        setupRecyclerView(shownUserProfileId)
        binding.viewModel = userViewModel
        loggedInUserEmail = currentUserProvider.getCurrentUserEmail()
        initSetup()
        setupViewAndButtonsAction()
        setupReportButton()
        setupRatingView()
        return binding.root
    }

    private fun initSetup() {
        val fields = listOf(
            binding.imageView,
            binding.gallerycameraholder,
            binding.nameText,
            binding.textEmail,
            binding.applyholder,
            binding.ratingTextview,
            binding.applyholder,
            binding.btnReport
        )
        for (view: View in fields) {
            view.visibility = View.GONE
        }
    }

    private fun setupRatingView(){
        userViewModel.refreshRating(shownUserProfileId)
        userViewModel.rating.observe(viewLifecycleOwner, {
            var text = resources.getString(R.string.default_rating)
            if(it > 0){
                text = String.format("%.2f", it)
            }
            binding.ratingTextview.text = text
        })
    }

    private fun setupButtonsVisibility() {
        val pictureButtonsRow = binding.gallerycameraholder
        if (currentUserId != null && isAuthUserDisplayedUser()) {
            pictureButtonsRow.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView(userId: String?) {
        val adapter = itemsViewModel.setupItemAdapter(currentUserId)
        binding.userItemList.adapter = adapter
        itemsViewModel.getUserItem(userId)
        itemsViewModel.addObserver(
            viewLifecycleOwner,
            adapter,
            ItemsViewModel.OBSERVABLES.USER_ITEMS
        )

        itemsViewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item -> UserProfileFragmentDirections.actionUserProfileFragmentToNewEditFragment(item) },
            { item ->
                UserProfileFragmentDirections.actionUserProfileFragmentToDetailedItemFragment(
                    item
                )
            })
    }

    private fun setupButtonsAction() {
        val buttons = listOf(binding.btnApply, binding.btnOpenCamera, binding.btnOpenGallery)
        for (button: Button in buttons) {
            button.setOnClickListener {
                getAction(button)
            }
        }
    }

    private fun setEmailText() {
        val emailText = binding.textEmail
        if (currentUserId != null && isAuthUserDisplayedUser()) {
            emailText.text = loggedInUserEmail
            emailText.visibility = View.VISIBLE
        }
    }

    private fun setupTopInfoVisibility() {
        val topInfoText = binding.upfTopinfo
        if (currentUserId != null || args.userId != null) {
            topInfoText.visibility = View.GONE
        }
    }

    private fun isAuthUserDisplayedUser(): Boolean {
        val shownUserId = args.userId
        return shownUserId == null || shownUserId == currentUserId
    }


    private fun setupImageAndNameVisibility() {
        val profilePictureImageView = binding.imageView
        val userDisplayName = binding.nameText
        val mainFields = listOf(profilePictureImageView, userDisplayName)
        for (view: View in mainFields) {
            if (currentUserId != null || args.userId != null) {
                view.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRatingVisibility(){
        if(currentUserId != null || args.userId != null){
            binding.ratingTextview.visibility = View.VISIBLE
        }
    }
    private fun setupViewAndButtonsAction() {
        setupButtonsAction()
        setupButtonsVisibility()
        setupTopInfoVisibility()
        setupImageAndNameVisibility()
        setupRatingVisibility()
    }

    private fun displayUserFields(requestedUser: User?) {
        if (requestedUser != null) {
            val requestedUserDisplayName = binding.nameText
            requestedUserDisplayName.text = requestedUser.name
            val userPictureUri = requestedUser.profilePicture
            val userProfilePicture = binding.imageView
            Glide.with(this).load(userPictureUri).into(userProfilePicture)
            setEmailText()
        }
    }

    private fun getAction(button: Button) {
        when (button) {
            binding.btnOpenCamera, binding.btnOpenGallery -> {
                binding.applyholder.visibility = View.VISIBLE
                if (button == binding.btnOpenGallery) imageAccess.openGallery()
                else imageAccess.openCamera()
            }
            binding.btnApply -> {
                imageUri = imageAccess.getImageUri()
                if (imageUri != Uri.EMPTY && imageUri != null) {
                    binding.imageView.setImageURI(imageUri)
                    lifecycleScope.launch(Dispatchers.IO) {
                        userRepository.add(
                            userRepository.get(currentUserId!!)!!
                                .copy(profilePicture = imageUri.toString())
                        )
                    }
                }
                binding.applyholder.visibility = View.GONE
            }
        }
    }

    private fun setupReportButton() {
        if (currentUserId != null) {
            binding.btnReport.visibility = View.VISIBLE
        }
        binding.btnReport.setOnClickListener { view ->
            view.findNavController().navigate(
                UserProfileFragmentDirections.actionUserProfileFragmentToReportFragment(
                    currentUserId!!, shownUserProfileId!!
                )
            )
        }
    }
}


