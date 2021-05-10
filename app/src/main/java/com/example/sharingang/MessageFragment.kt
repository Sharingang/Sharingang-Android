package com.example.sharingang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.users.User
import com.example.sharingang.users.UserAdapter
import com.example.sharingang.users.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var partnerId: String
    private lateinit var partnerProfilePic: String
    private lateinit var partnerUsername: String
    private lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private val args: MessageFragmentArgs by navArgs()
    private val messagesLiveData: MutableLiveData<List<String>> = MutableLiveData(listOf())

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        binding.history.layoutManager = LinearLayoutManager(requireContext())
        messagesLiveData.observe(viewLifecycleOwner, { newList ->
            (binding.history.adapter as MessageAdapter).submitList(newList)
        })
        partnerId = args.partnerId
        partnerUsername = args.partnerUsername
        partnerProfilePic = args.partnerProfilePictureUrl
        currentUser = auth.currentUser!!
        setupFields()
        setupSendButton()
        setupUI()
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
            "message" to message,
        )
        val lastTimeChat = hashMapOf<String, Any>(
            "lastTime" to Date()
        )
        firebaseFirestore.collection("users").document(from)
            .collection("chats").document(to).collection("messages")
            .document(Date().toString()).set(data)
        firebaseFirestore.collection("users").document(to)
            .collection("chats").document(from).collection("messages")
            .document(Date().toString()).set(data)
        binding.messageEditText.text.clear()
        firebaseFirestore.collection("users").document(from)
            .collection("messagePartners").document(to).set(lastTimeChat)
        firebaseFirestore.collection("users").document(to)
            .collection("messagePartners").document(from).set(lastTimeChat)
    }

    private fun setupUI() {
        val listMessages = mutableListOf<String>()
        messageAdapter = MessageAdapter(requireContext(), listMessages)
        binding.history.adapter = messageAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            val messages = firebaseFirestore.collection("users")
                .document(currentUser.uid).collection("chats")
                .document(partnerId).collection("messages")
                .get().await()
            listMessages.clear()
            for (document in messages.documents) {
                val message = document.getString("message")
                if(message != null) {
                    listMessages.add(message)
                }
            }
            messagesLiveData.postValue(listMessages)
        }
    }
}