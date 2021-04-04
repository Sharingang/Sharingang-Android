package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.UserProfileFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val viewModel: UserProfileViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserProfileFragmentBinding.inflate(inflater, container, false)

        viewModel.setUser(args.userId)
        binding.viewModel = viewModel

        /*
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.createTestUser(args.userId)
        }
        */
        viewModel.user.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                binding.nameText.text = user.name
                Glide.with(this).load(user.profilePicture).into(binding.imageView)
            }
        })

        return binding.root
    }

}
