package com.example.sharingang


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.AuthHelper
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ImageAccess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private lateinit var authHelper: AuthHelper

    // This is the currently logged in user
    private var currentUserId: String? = null
    private lateinit var imageAccess: ImageAccess

    private var currentUser: FirebaseUser? = null
    private lateinit var userType: UserType

    // This is the user whose profile is shown (can be different from currentUserId)
    private var shownUserProfileId: String? = null
    private var loggedInUserEmail: String? = null
    private var imageUri: Uri? = null

    private enum class UserType {
        LOGGED_OUT_SELF,
        LOGGED_OUT,
        VISITOR,
        SELF
    }

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var auth: FirebaseAuth

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
        authHelper = AuthHelper(
            requireContext(), auth, lifecycleScope, userRepository, this, currentUserProvider
        ) { user: FirebaseUser, userId: String -> execAfterSignIn(user, userId) }
        currentUser = auth.currentUser
        setUserType()
        userViewModel.setUser(shownUserProfileId)
        imageAccess = ImageAccess(requireActivity())
        imageAccess.setupImageView(binding.imageView)
        lifecycle.addObserver(imageAccess)
        binding.viewModel = userViewModel
        initializeFields()
        return binding.root
    }

    private fun initializeFields() {
        currentUserId = currentUserProvider.getCurrentUserId()
        setUserType()
        loggedInUserEmail = currentUserProvider.getCurrentUserEmail()
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            displayUserFields(user)
        })
        setupRecyclerView(shownUserProfileId)
        setupAuthenticationButtons()
        initSetup()
        setEmailText()
        setVisibilities()
        setupViews()
        setupReportButton()
        setupRatingView()
    }

    private fun setUserType() {
        userType = when (currentUserId) {
            null -> if(shownUserProfileId == null) UserType.LOGGED_OUT_SELF else UserType.LOGGED_OUT
            shownUserProfileId -> UserType.SELF
            else -> UserType.VISITOR
        }
    }

    private fun initSetup() {
        listOf(binding.upfTopinfo, binding.imageView, binding.gallerycameraholder, binding.nameText,
            binding.textEmail, binding.applyholder, binding.ratingTextview, binding.applyholder,
            binding.btnReport, binding.userItemList, binding.btnLogout,
            binding.btnLogin
        ).forEach { view -> view.visibility = View.GONE }
    }

    private fun getVisibleViews(): List<View> {
        return when(userType) {
            UserType.LOGGED_OUT -> listOf(
                    binding.imageView, binding.nameText, binding.ratingTextview, binding.userItemList)
            UserType.VISITOR -> listOf(
                    binding.imageView, binding.nameText, binding.ratingTextview,
                    binding.userItemList, binding.btnReport)
            UserType.SELF -> listOf(
                    binding.imageView, binding.nameText, binding.ratingTextview, binding.userItemList,
                binding.textEmail, binding.gallerycameraholder, binding.btnLogout)
            else -> listOf(binding.upfTopinfo, binding.btnLogin)
        }
    }

    private fun setVisibilities() {
        val visibleViews = getVisibleViews()
        visibleViews.forEach { view -> view.visibility = View.VISIBLE }
    }


    private fun setupRatingView(){
        userViewModel.refreshRating(shownUserProfileId)
        userViewModel.rating.observe(viewLifecycleOwner, {
            var text = resources.getString(R.string.default_rating)
            if (it > 0) text = String.format("%.2f", it)
            binding.ratingTextview.text = text
        })
    }

    private fun setupRecyclerView(userId: String?) {
        val adapter = itemsViewModel.setupItemAdapter(currentUserId)
        binding.userItemList.adapter = adapter
        itemsViewModel.getUserItem(userId)
        itemsViewModel.addObserver(viewLifecycleOwner, adapter, ItemsViewModel.OBSERVABLES.USER_ITEMS)
        itemsViewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item ->
                UserProfileFragmentDirections.actionUserProfileFragmentToDetailedItemFragment(
                    item
                )
            })
    }

    private fun setupButtonsAction() {
        val buttons = listOf(binding.btnApply, binding.btnOpenCamera, binding.btnOpenGallery)
        buttons.forEach { button -> button.setOnClickListener {setupPictureButton(button)} }
    }

    private fun setEmailText() {
        val emailText = binding.textEmail
        if (userType == UserType.SELF) {
            emailText.text = loggedInUserEmail
        }
    }

    private fun setupViews() {
        setupButtonsAction()
        setUpfTopInfoText()
        setEmailText()
    }

    private fun setUpfTopInfoText() {
        val topInfo = binding.upfTopinfo
        topInfo.text = getString(R.string.userNotLoggedInInfo)
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

    private fun setupPictureButton(button: Button) {
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
        if(userType == UserType.VISITOR) {
            lifecycleScope.launch(Dispatchers.IO) {
                val hasBeenReported =
                    userRepository.hasBeenReported(currentUserId!!, shownUserProfileId!!)
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.btnReport.visibility =
                        if (hasBeenReported) View.GONE
                        else View.VISIBLE
                }
            }
            binding.btnReport.setOnClickListener { view ->
                view.findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToReportFragment(
                        currentUserId!!, shownUserProfileId!!, binding.nameText.text.toString()
                    )
                )
            }
        }
    }

    private fun execAfterSignIn(loggedInUser: FirebaseUser, loggedInUserId: String) {
        currentUser = loggedInUser
        currentUserId = loggedInUserId
        shownUserProfileId = loggedInUserId
        initializeFields()
        binding.nameText.text = currentUser!!.displayName
        if(currentUser!!.photoUrl != null) {
            // Use the Glide image loader library to load the user's picture into the imageView
            Glide.with(this).load(currentUser!!.photoUrl).into(binding.imageView)
        }
    }

    private fun setupAuthenticationButtons() {
        binding.btnLogin.setOnClickListener {
            authHelper.signIn()
        }
        binding.btnLogout.setOnClickListener {
            authHelper.signOut()
            initSetup()
            userType = UserType.LOGGED_OUT_SELF
            setupViews()
            setVisibilities()}
    }

}


