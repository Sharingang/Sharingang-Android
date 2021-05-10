package com.example.sharingang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

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
        setupSendButton()
        return binding.root
    }

    private fun setupFields() {
        Glide.with(this).load(partnerProfilePic).into(binding.chatPartnerPic)
        binding.chatPartnerUsername.text = partnerUsername
        Log.e("xxx", "name = $partnerUsername")
    }

    private fun setupSendButton() {
        binding.btnSend.isEnabled = false
        binding.messageEditText.addTextChangedListener {
            binding.btnSend.isEnabled = !it.isNullOrBlank()
        }
        binding.btnSend.setOnClickListener {
            val message: String = binding.messageEditText.text.toString()
            sendMessage(currentUser.uid, partnerId, message)
        }
    }

    private fun sendMessage(from: String, to: String, message: String) {
        val data = hashMapOf<String, Any>(
            "from" to from,
            "to" to to,
            "message" to message,
            "date" to Date()
        )
        firebaseFirestore.collection("users").document(from)
            .collection("chats").document(to).collection("messages")
            .add(data)
        firebaseFirestore.collection("users").document(to)
            .collection("chats").document(from).collection("messages")
            .add(data)
        binding.messageEditText.text.clear()
    }
}