package com.example.sharingang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var partnerId: String
    private lateinit var partnerProfilePic: String
    private lateinit var partnerUsername: String
    private lateinit var binding: FragmentMessageBinding
    private val args: MessageFragmentArgs by navArgs()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        partnerId = args.partnerId
        partnerUsername = args.partnerUsername
        partnerProfilePic = args.partnerProfilePictureUrl
        currentUser = auth.currentUser!!
        setupFields()
        return binding.root
    }

    private fun setupFields() {
        Glide.with(requireActivity()).load(partnerProfilePic).into(binding.chatPartnerPic)
        binding.chatPartnerUsername.text = partnerUsername
    }
}