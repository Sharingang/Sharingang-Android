package com.example.sharingang


import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private var currentUserId: String? = ""
    private lateinit var imageAccess: ImageAccess
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    @Inject
    lateinit var userRepository: UserRepository
    private val TESTURI = "https://picsum.photos/200"

    companion object {
        fun newInstance() = UserProfileFragment()
        var imageUri: Uri? = null
        var isTestCase: Boolean = false
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
        isTestCase = false
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
        setupButtonsVisibility()
        return binding.root
    }

    private fun setupButtonsVisibility() {
        currentUserId = currentUserProvider.getCurrentUserId()
        val buttons = listOf(binding.btnOpenGallery, binding.btnOpenCamera, binding.btnApply)
        for(button: Button in buttons) {
            button.visibility =
                if(currentUserId != null) View.VISIBLE
                else View.GONE
        }
        binding.btnApply.visibility = View.GONE
        setupButtons()

    }
    private fun setupButtons() {
        val buttons = listOf(binding.btnApply, binding.btnOpenCamera, binding.btnOpenGallery)
        for(button: Button in buttons) {
            button.setOnClickListener {
                binding.btnApply.visibility =
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
                imageUri = if (!isTestCase) imageAccess.getImageUri() else Uri.parse(TESTURI)
                if (imageUri != Uri.EMPTY && imageUri != null) {
                    binding.imageView.setImageURI(imageUri)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val currentUser = userRepository.get(currentUserId!!)
                        val updatedUser = currentUser!!.copy(profilePicture = imageUri.toString())
                        userRepository.add(updatedUser)
                    }
                }
                binding.btnApply.visibility = View.GONE
            }
        }
    }


}


