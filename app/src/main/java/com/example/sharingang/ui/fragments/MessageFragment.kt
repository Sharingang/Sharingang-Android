package com.example.sharingang.ui.fragments

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
import com.example.sharingang.models.Chat
import com.example.sharingang.ui.adapters.MessageAdapter
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private var listChats: MutableList<Chat> = mutableListOf()
    private var shouldUpdate = false

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
        messagesLiveData.postValue(listChats)
        lifecycleScope.launch(Dispatchers.Main) {
            setupMessageRefresh()
        }
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
            listChats.clear()
            lifecycleScope.launch(Dispatchers.IO) {
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
        listChats.clear()
        shouldUpdate = false
        listChats.addAll(userRepository.putMessage(from, to, message))
        shouldUpdate = true
        messagesLiveData.postValue(listChats)
    }

    /**
     * Sets up the UI of the fragment (adapter + messages)
     */
    private fun setupUI() {
        listChats.clear()
        messageAdapter = MessageAdapter(requireContext(), listChats, currentUserId, this)
        binding.history.adapter = messageAdapter
        getAndDisplayMessages()
    }

    /**
     * Adds a change listener to the document where the last chat time is stored.
     */
    private suspend fun setupMessageRefresh() {
        userRepository.setupRefresh(currentUserId, partnerId) {
            if(isAdded && shouldUpdate) {
                getAndDisplayMessages()
            }
        }
    }

    /**
     * Gets the messages and displays them
     */
    fun getAndDisplayMessages() {
        lifecycleScope.launch(Dispatchers.Main) {
            shouldUpdate = false
            userRepository.clearNumUnread(currentUserId, partnerId)
            shouldUpdate = true
            listChats.clear()
            listChats.addAll(userRepository.getMessages(currentUserId, partnerId))
            messagesLiveData.postValue(listChats)
        }
    }

    /**
     * Scrolls to the end of messages
     */
    fun scrollToEnd() {
        binding.history.scrollToPosition(listChats.size - 1)
    }
}
