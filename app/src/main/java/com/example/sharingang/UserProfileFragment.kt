package com.example.sharingang


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ImageAccess
import com.google.firebase.firestore.remote.Datastore

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.prefs.Preferences
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    private lateinit var imageAccess: ImageAccess
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

        // If no userId is provided, we get the user that is currently logged in.
        val userId = when(args.userId) {
            null, "" -> currentUserProvider.getCurrentUserId()
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
        val buttons = listOf(binding.btnOpenGallery, binding.btnOpenCamera, binding.btnApply)
        for(button: Button in buttons) {
            button.visibility =
                if(currentUserProvider.getCurrentUserId() != null) View.VISIBLE
                else View.GONE
        }
        binding.btnApply.visibility = View.GONE
        binding.btnOpenGallery.setOnClickListener {
            imageAccess.openGallery()
            binding.btnApply.visibility = View.VISIBLE



        }
        binding.btnOpenCamera.setOnClickListener {
            imageAccess.openCamera()
            binding.btnApply.visibility = View.VISIBLE


        }
        binding.btnApply.setOnClickListener {
            imageUri = imageAccess.getImageUri()
            if (imageUri != Uri.EMPTY && imageUri != null) {
                binding.imageView.setImageURI(imageUri)
                lifecycleScope.launch(Dispatchers.IO) {
                    val user = userRepository.get(currentUserProvider.getCurrentUserId()!!)
                    val updatedUser = user!!.copy(
                        id = user.id,
                        name = user.name,
                        profilePicture = imageUri.toString()
                    )
                    userRepository.add(updatedUser)

                }
            }
            binding.btnApply.visibility = View.GONE
        }
    }


}


