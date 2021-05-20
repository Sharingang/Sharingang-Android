package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.sharingang.models.Chat
import com.example.sharingang.ui.adapters.MessageAdapter
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject


/**
 * MessageFragment takes care of representing and displaying
 * the messages in a view for a pair of communicating users.
 */
@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var currentUserId: String
    private lateinit var partnerId: String
    private var partnerProfilePic: String? = null
    private lateinit var partnerUsername: String
    private lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private val args: MessageFragmentArgs by navArgs()
    private val messagesLiveData: MutableLiveData<List<Chat>> = MutableLiveData(listOf())
    private val listChats: MutableList<Chat> = mutableListOf()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

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
        lifecycleScope.launch(Dispatchers.Main) {
            setupMessageRefresh()
        }
        messagesLiveData.postValue(listChats)
        return binding.root
    }

    /**
     * Setup the views for the profile picture and the username
     * of the chat partner on the top of the fragment.
     */
    private fun setupFields() {
        Glide.with(this).load(partnerProfilePic).into(binding.chatPartnerPic)
        binding.chatPartnerUsername.text = partnerUsername
    }

    /**
     * Setup the View and action for the button used for sending a message.
     */
    private fun setupSendButton() {
        binding.btnSend.isEnabled = false
        binding.messageEditText.addTextChangedListener {
            binding.btnSend.isEnabled = !it.isNullOrBlank()
        }
        binding.btnSend.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            binding.messageEditText.text.clear()
            lifecycleScope.launch(Dispatchers.Main) {
                sendMessage(currentUserId, partnerId, message)
            }
        }
    }

    /**
     * Puts a message into the database and clears the message text field.
     *
     * @param from the sender
     * @param to the receiver
     * @param message the message to send
     */
    private suspend fun sendMessage(from: String, to: String, message: String) {
        val newMessages = userRepository.putMessage(from, to, message)
        messagesLiveData.postValue(newMessages)
    }

    /**
     * Sets up the UI of the fragment
     */
    private fun setupUI() {
        listChats.clear()
        messageAdapter = MessageAdapter(requireContext(), listChats, currentUserId)
        binding.history.adapter = messageAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            getAndDisplayMessages()
            lifecycleScope.launch(Dispatchers.Main) {
                scrollToEnd()
            }
        }
    }

    /**
     * Adds a change listener to the document where the last chat time is stored.
     */
    private suspend fun setupMessageRefresh() {
        userRepository.setupRefresh(currentUserId, partnerId, this, lifecycleScope)
    }

    /**
     * Gets the messages and displays them with the help of the adapter
     */
    suspend fun getAndDisplayMessages() {
        listChats.clear()
        val messages = userRepository.getMessages(currentUserId, partnerId)
        messagesLiveData.postValue(messages)
    }

    /**
     * Scrolls to the end of messages
     */
    fun scrollToEnd() {
        if (listChats.isNotEmpty()) {
            Log.e("xxx", "Size = ${listChats.size}")
            binding.history.scrollToPosition(listChats.size - 1)
        }
        else {
            Log.e("xxx", "Chat is empty")
        }
    }
}
