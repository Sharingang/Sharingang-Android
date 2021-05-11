package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.UserRepository
import com.google.firebase.firestore.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var currentUserId: String
    private lateinit var partnerId: String
    private lateinit var partnerProfilePic: String
    private lateinit var partnerUsername: String
    private lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private val args: MessageFragmentArgs by navArgs()
    private val messagesLiveData: MutableLiveData<List<Chat>> = MutableLiveData(listOf())
    private lateinit var listChats: MutableList<Chat>

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    private var firebaseFirestore = FirebaseFirestore.getInstance()

    @Inject
    lateinit var userRepository: UserRepository

    private class LinkedPair<A>(private val fst: A, private val snd: A) {
        fun otherOf(x: A): A? {
            return if(x == fst) snd else if(x == snd) fst else null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        binding.history.layoutManager = LinearLayoutManager(requireContext())
        (binding.history.layoutManager as LinearLayoutManager).stackFromEnd = true
        messagesLiveData.observe(viewLifecycleOwner, { newList ->
            (binding.history.adapter as MessageAdapter).submitList(newList)
        })
        partnerId = args.partnerId
        partnerUsername = args.partnerUsername
        partnerProfilePic = args.partnerProfilePictureUrl
        currentUserId = currentUserProvider.getCurrentUserId()!!
        setupFields()
        setupSendButton()
        setupUI()
        setupMessageRefresh()
        return binding.root
    }

    private fun setupFields() {
        Glide.with(this).load(partnerProfilePic).into(binding.chatPartnerPic)
        binding.chatPartnerUsername.text = partnerUsername
    }

    private fun setupSendButton() {
        binding.btnSend.isEnabled = false
        binding.messageEditText.addTextChangedListener {
            binding.btnSend.isEnabled = !it.isNullOrBlank()
        }
        binding.btnSend.setOnClickListener {
            val message: String = binding.messageEditText.text.toString()
            sendMessage(currentUserId, partnerId, message)
            setupUI()
        }
    }

    private fun getUserDocument(userId: String): DocumentReference {
        val usersCollection = firebaseFirestore.collection(getString(R.string.users))
        return usersCollection.document(userId)
    }

    private fun sendMessage(from: String, to: String, message: String) {
        val data = hashMapOf<String, Any>(
            getString(R.string.message) to message,
            getString(R.string.from) to from,
            getString(R.string.to) to to
        )
        val lastTimeChat = hashMapOf<String, Any>(
            getString(R.string.last_message) to "${Date()} (${System.currentTimeMillis()})"
        )
        val fromToPair = LinkedPair(from, to)
        listOf(from, to).forEach {
            val userDocument = getUserDocument(it)
            userDocument.collection(getString(R.string.chats)).document(fromToPair.otherOf(it)!!)
                .collection(getString(R.string.messages)).document(Date().toString()).set(data)
            userDocument.collection(getString(R.string.messagePartners))
                .document(fromToPair.otherOf(it)!!).set(lastTimeChat)
        }
        binding.messageEditText.text.clear()
    }

    private fun setupUI() {
        listChats = mutableListOf()
        messageAdapter = MessageAdapter(requireContext(), listChats, currentUserId)
        binding.history.adapter = messageAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            userRepository.refreshUsers()
            getAndDisplayMessages()
        }
    }

    private fun addMessagesToChatList(documents: MutableList<DocumentSnapshot>) {
        for (document in documents) {
            val message = document.getString(getString(R.string.message))
            if (message != null) {
                listChats.add(
                    Chat(
                        document.getString(getString(R.string.from)),
                        document.getString(getString(R.string.to)),
                        message))
                messagesLiveData.postValue(listChats)
            }
        }
    }

    private fun setupMessageRefresh() {
        val lastTimeChatDocument = getUserDocument(currentUserId)
            .collection(getString(R.string.messagePartners)).document(partnerId)
        addOnChangeListener(lastTimeChatDocument)
    }

    private fun addOnChangeListener(ref: DocumentReference) {
        ref.addSnapshotListener { _, e ->
            if (e == null) {
                setupUI()
            }
        }
    }

    private suspend fun getAndDisplayMessages() {
        listChats.clear()
        val messages = firebaseFirestore.collection(getString(R.string.users))
            .document(currentUserId).collection(getString(R.string.chats))
            .document(partnerId).collection(getString(R.string.messages))
            .get().await()
        addMessagesToChatList(messages.documents)
        messagesLiveData.postValue(listChats)
        lifecycleScope.launch(Dispatchers.Main) {
            if (listChats.isNotEmpty()) {
                binding.history.scrollToPosition(listChats.size - 1)
            }
        }
    }
}