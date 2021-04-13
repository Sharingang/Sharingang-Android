package com.example.sharingang

import android.R
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.gesture.GestureLibraries.fromFile
import android.net.Uri
import android.net.Uri.fromFile
import android.net.Uri.parse
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.TypefaceCompatUtil.getTempFile
import androidx.documentfile.provider.DocumentFile.fromFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ByteBufferUtil.fromFile
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.utils.ImageAccess
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromFile
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    private lateinit var imageAccess: ImageAccess
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

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
        val buttons = Arrays.asList(binding.btnOpenGallery, binding.btnOpenCamera)
        for(button: Button in buttons) {
            button.visibility =
                if(currentUserProvider.getCurrentUserId() != null) View.VISIBLE
                else View.GONE
            val imageUri = imageAccess.getImageUri()
            button.setOnClickListener {
                binding.imageView.setImageURI(imageUri)
            }
        }
        binding.btnOpenGallery.setOnClickListener {
            imageAccess.openGallery()
        }
        binding.btnOpenCamera.setOnClickListener {
            imageAccess.openCamera()
        }
    }


}
