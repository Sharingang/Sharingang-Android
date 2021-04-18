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
    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    private lateinit var imageAccess: ImageAccess
    private var imageUri: Uri? = null
    private var currentUserId: String? = ""
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
        val userId = when(args.userId) {
            null, "" -> currentUserId
                else -> args.userId

        }
        imageAccess = ImageAccess(requireActivity())
        imageAccess.setupImageView(binding.imageView)
        lifecycle.addObserver(imageAccess)
        viewModel.setUser(userId)
        viewModel.user.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                binding.nameText.text = user.name
                Glide.with(this).load(user.profilePicture).into(binding.imageView)
            }
        })
        binding.viewModel = viewModel
        setupPfpButtons()
        return binding.root
    }

    private fun setupPfpButtons() {
        currentUserId = currentUserProvider.getCurrentUserId()
        val buttons = listOf(binding.btnOpenGallery, binding.btnOpenCamera, binding.btnApply)
        for(button: Button in buttons) {
            button.visibility =
                if(currentUserId != null) View.VISIBLE
                else View.GONE
        }
        binding.btnApply.visibility = View.GONE
        setupActionButtons()
        setupApplyButton()
    }
    private fun setupActionButtons() {
        binding.btnOpenGallery.setOnClickListener {
            binding.btnApply.visibility = View.VISIBLE
            imageAccess.openCamera()
        }
        binding.btnOpenCamera.setOnClickListener {
            binding.btnApply.visibility = View.VISIBLE
            imageAccess.openCamera()
        }
        /*
        for(button: Button in buttons) {
            button.setOnClickListener {
                binding.btnApply.visibility = View.VISIBLE
                if(button == binding.btnOpenCamera) imageAccess.openCamera()
                else imageAccess.openGallery()
            }
        }
        */
    }

    private fun setupApplyButton() {
        binding.btnApply.setOnClickListener {
            imageUri = imageAccess.getImageUri()
            if (imageUri != Uri.EMPTY && imageUri != null) {
                binding.imageView.setImageURI(imageUri)
                lifecycleScope.launch(Dispatchers.IO) {
                    userRepository.add(userRepository.get(currentUserId!!)!!
                        .copy(profilePicture = imageUri.toString()))
                }
            }
            binding.btnApply.visibility = View.GONE
        }
    }


}


