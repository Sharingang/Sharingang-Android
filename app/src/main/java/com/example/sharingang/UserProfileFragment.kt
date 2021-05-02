package com.example.sharingang


import android.app.Activity
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ImageAccess
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
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

    private var currentUser: FirebaseUser? = null
    private lateinit var userType: UserType

    // This is the user whose profile is shown (can be different from currentUserId)
    private var shownUserProfileId: String? = null
    private var loggedInUserEmail: String? = null
    private var imageUri: Uri? = null

    private enum class UserType {
        LOGGED_OUT,
        VISITOR,
        SELF,
        LOGGED_OUT_SELF
    }

    private enum class AccountStatus {
        LOGGED_IN,
        LOGGED_OUT
    }

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                update(AccountStatus.LOGGED_IN, currentUserProvider.getCurrentUser())
            } else {
                update(AccountStatus.LOGGED_OUT, null)
            }
        }

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
        currentUser = currentUserProvider.getCurrentUser()
        setUserType()
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
        setupButtons()
        restoreLoginStatus()
        initSetup()
        setVisibilities()
        setupViews()
        setupReportButton()
        setupRatingView()
        return binding.root
    }

    private fun setUserType() {
        userType = when (currentUserId) {
            null -> UserType.LOGGED_OUT
            shownUserProfileId -> UserType.SELF
            else -> UserType.VISITOR
        }
        if(userType == UserType.LOGGED_OUT && shownUserProfileId == null) {
            userType = UserType.LOGGED_OUT_SELF
        }
        Log.d(TAG, "UserType = ${userType}")
    }

    private fun initSetup() {
        val fields = listOf(
            binding.upfTopinfo,
            binding.imageView,
            binding.gallerycameraholder,
            binding.nameText,
            binding.textEmail,
            binding.applyholder,
            binding.ratingTextview,
            binding.applyholder,
            binding.btnReport,
            binding.userItemList,
            binding.btnLogout,
            binding.btnLogin
        )
        for (view: View in fields) {
            view.visibility = View.GONE
        }
    }

    private fun getVisibleViews(): List<View> {
        return when(userType) {
            UserType.LOGGED_OUT ->
                listOf(
                    binding.imageView, binding.nameText,
                    binding.ratingTextview, binding.userItemList
                )
            UserType.VISITOR ->
                listOf(
                    binding.imageView, binding.nameText, binding.ratingTextview,
                    binding.userItemList, binding.btnReport
                )
            UserType.SELF ->
                listOf(binding.imageView, binding.nameText, binding.ratingTextview,
                    binding.userItemList, binding.textEmail, binding.gallerycameraholder,
                    binding.btnLogout
                )
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
            if(it > 0){
                text = String.format("%.2f", it)
            }
            binding.ratingTextview.text = text
        })
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

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        resultLauncher.launch(intent)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                update(AccountStatus.LOGGED_OUT, null)
            }
    }

    private fun update(accountStatus: AccountStatus, user: FirebaseUser?) {
        if (accountStatus == AccountStatus.LOGGED_IN) {
            addUserToDatabase(user!!)
        }
    }

    private fun restoreLoginStatus() {
        if (currentUser != null) update(AccountStatus.LOGGED_IN, currentUser)
        else update(AccountStatus.LOGGED_OUT, null)
    }

    private fun addUserToDatabase(user: FirebaseUser) {
        val userToConnectId = user.uid
        lifecycleScope.launch(Dispatchers.IO) {
            if (userRepository.get(userToConnectId) == null) {
                userRepository.add(
                    User(
                        id = userToConnectId,
                        name = user.displayName!!,
                        profilePicture = user.photoUrl?.toString()
                    )
                )
            }
        }
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            signIn()
        }
        binding.btnLogout.setOnClickListener {
            signOut()
        }
    }
}


