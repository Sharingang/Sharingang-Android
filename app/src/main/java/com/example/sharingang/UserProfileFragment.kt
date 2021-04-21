package com.example.sharingang


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
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
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    // This is the currently logged in user
    private var currentUserId: String? = ""
    private lateinit var imageAccess: ImageAccess
    // This is the user whose profile is shown (can be different from currentUserId)
    private var shownUserProfileId: String? = null
    private var imageUri: Uri? = null
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    @Inject
    lateinit var userRepository: UserRepository

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileFragmentBinding.inflate(inflater, container, false)
        currentUserId = currentUserProvider.getCurrentUserId()
        // If no userId is provided, we get the user that is currently logged in.
        shownUserProfileId = when(args.userId) {
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
        binding.viewModel = userViewModel
        setupViewAndButtons()
        return binding.root
    }

    private fun setupButtonsVisibility() {
        currentUserId = currentUserProvider.getCurrentUserId()
        val pictureButtonsRow = binding.gallerycameraholder
        val applyButtonRow = binding.applyholder
        pictureButtonsRow.visibility =
            if(currentUserId != null && isAuthUserDisplayedUser()) View.VISIBLE
            else View.GONE
        applyButtonRow.visibility = View.GONE
    }
    private fun setupButtons() {
        val buttons = listOf(binding.btnApply, binding.btnOpenCamera, binding.btnOpenGallery)
        for(button: Button in buttons) {
            button.setOnClickListener {
                when (button) {
                    binding.btnOpenCamera, binding.btnOpenGallery -> {
                        binding.applyholder.visibility = View.VISIBLE
                        if(button == binding.btnOpenGallery) imageAccess.openGallery()
                        else imageAccess.openCamera()
                    }
                    binding.btnApply -> {
                        imageUri = imageAccess.getImageUri()
                        if (imageUri != Uri.EMPTY && imageUri != null) {
                            binding.imageView.setImageURI(imageUri)
                            lifecycleScope.launch(Dispatchers.IO) {
                                userRepository.add(userRepository.get(currentUserId!!)!!.copy(profilePicture = imageUri.toString()))
                            }
                        }
                        binding.applyholder.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setEmailText() {
        val emailText = binding.textEmail
        emailText.text =
            if(isAuthUserDisplayedUser() && currentUserId != null)
                currentUserProvider.getCurrentUserEmail()
            else "e-mail not available"
        binding.textEmail.visibility =
            if(currentUserId != null) View.VISIBLE
            else View.GONE
    }

    private fun setupTopInfoVisibility() {
        val topInfoText = binding.upfTopinfo
        topInfoText.visibility =
            if(currentUserProvider.getCurrentUserId() != null) View.GONE
            else View.VISIBLE
    }

    private fun isAuthUserDisplayedUser(): Boolean {
        val shownUserId = args.userId
        return shownUserId == null || shownUserId == currentUserId
    }

    private fun setupPfpAndNameVisibility() {
        val profilePictureImageView = binding.imageView
        val userDisplayName = binding.nameText
        val mainFields = listOf(profilePictureImageView, userDisplayName)
        for(view: View in mainFields) {
            view.visibility =
                if(currentUserId == null) View.GONE
                else View.VISIBLE
        }
    }

    private fun setupViewAndButtons() {
        setupButtons()
        setupButtonsVisibility()
        setupTopInfoVisibility()
        setupPfpAndNameVisibility()
    }

    private fun displayUserFields(user: User?) {
        if (user != null) {
            binding.nameText.text = user.name
            Glide.with(this).load(user.profilePicture).into(binding.imageView)
            setEmailText()
        }
    }

}


