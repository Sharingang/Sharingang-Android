package com.example.sharingang


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ImageAccess
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

import javax.inject.Inject



@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val userViewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    private var currentUserId: String? = ""
    private lateinit var imageAccess: ImageAccess
    private var imageUri: Uri? = null
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var auth: FirebaseAuth

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
        val userId = when(args.userId) {
            null, "" -> currentUserId
            else -> args.userId
        }
        userViewModel.setUser(userId)
        imageAccess = ImageAccess(requireActivity())
        imageAccess.setupImageView(binding.imageView)
        lifecycle.addObserver(imageAccess)
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                binding.nameText.text = user.name
                Glide.with(this).load(user.profilePicture).into(binding.imageView)
                setEmailText()
            }
        })
        binding.viewModel = userViewModel
        setupButtonsVisibility()
        setupTopInfoVisibility()
        setupPfpAndNameVisibility()
        return binding.root
    }

    private fun setupButtonsVisibility() {
        currentUserId = currentUserProvider.getCurrentUserId()
        val buttons = listOf(binding.btnOpenGallery, binding.btnOpenCamera, binding.btnApply)
        binding.gallerycameraholder.visibility =
            if(currentUserId != null && isAuthUserDisplayedUser()) View.VISIBLE
            else View.GONE
        binding.applyholder.visibility = View.GONE
        setupButtons()

    }
    private fun setupButtons() {
        val buttons = listOf(binding.btnApply, binding.btnOpenCamera, binding.btnOpenGallery)
        for(button: Button in buttons) {
            button.setOnClickListener {
                binding.applyholder.visibility =
                    if(button == binding.btnOpenCamera || button == binding.btnOpenGallery)
                        View.VISIBLE
                    else View.GONE
                getAction(button)
            }
        }
    }

    private fun getAction(button: Button) {
        when (button) {
            binding.btnOpenCamera -> imageAccess.openCamera()
            binding.btnOpenGallery -> imageAccess.openGallery()
            binding.btnApply -> {
                imageUri = imageAccess.getImageUri()
                if (imageUri != Uri.EMPTY && imageUri != null) {
                    binding.imageView.setImageURI(imageUri)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val currentUser = userRepository.get(currentUserId!!)
                        val updatedUser = currentUser!!.copy(profilePicture = imageUri.toString())
                        userRepository.add(updatedUser)
                    }
                }
                binding.applyholder.visibility = View.GONE
            }
        }
    }

    private fun setEmailText() {
        binding.textEmail.text =
            if(isAuthUserDisplayedUser() && currentUserId != null)
                currentUserProvider.getCurrentUserEmail()
            else "e-mail not available"
        binding.textEmail.visibility = if(currentUserId != null) View.VISIBLE
        else View.GONE
    }

    private fun setupTopInfoVisibility() {
        binding.upfTopinfo.visibility =
            if(currentUserProvider.getCurrentUserId() != null) View.GONE
            else View.VISIBLE
    }

    private fun isAuthUserDisplayedUser(): Boolean {
        return args.userId == null || args.userId == currentUserId
    }

    private fun setupPfpAndNameVisibility() {
        val mainFields = listOf(binding.imageView, binding.nameText)
        for(view: View in mainFields) {
            view.visibility = if(currentUserId == null) View.GONE else View.VISIBLE
        }
    }

}


