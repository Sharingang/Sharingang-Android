package com.example.sharingang

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.users.CurrentUserProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                handle_success(result)
            }
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

        viewModel.setUser(userId)

        viewModel.user.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                binding.nameText.text = user.name
                Glide.with(this).load(user.profilePicture).into(binding.imageView)
            }
        })

        binding.viewModel = viewModel
        setupPfpButton()
        binding.btnChangePfp.setOnClickListener() {
            changeProfilePicture()
        }

        return binding.root
    }

    private fun changeProfilePicture() {
        val pickPhotoIntent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(pickPhotoIntent)
    }

    private fun setupPfpButton() {
        binding.btnChangePfp.visibility =
                if (currentUserProvider.getCurrentUserId() != null) View.VISIBLE
                else View.GONE
    }

    private fun handle_success(result: ActivityResult) {
        binding.imageView.setImageURI(result.data!!.data)
    }

}
