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


/**
 * MessageFragment takes care of representing and displaying
 * the messages in a view for a pair of communicating users.
 */
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

    /**
     * This class is useful for creating a pair where we can retrieve
     * one value from the other.
     */
    private class LinkedPair<A>(private val fst: A, private val snd: A) {
        /**
         * Retrieve the pair's other element
         *
         * @param x The pair's element whose partner we want to get
         * @return The pair's partner (null if x is not part of the pair)
         */
        fun otherOf(x: A): A? {
            return if (x == fst) snd else if (x == snd) fst else null
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
        listChats = mutableListOf()
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
            val message: String = binding.messageEditText.text.toString()
            sendMessage(currentUserId, partnerId, message)
        }
    }

    /**
     * Retrieve the user's document from the database based on its id.
     *
     * @param userId the id of the user we want to get the document of
     * @return the document of the user
     */
    private fun getUserDocument(userId: String): DocumentReference {
        val usersCollection = firebaseFirestore.collection(getString(R.string.users))
        return usersCollection.document(userId)
    }

    /**
     * Puts a message into the database and clears the message text field.
     *
     * @param from the sender
     * @param to the receiver
     * @param message the message to send
     */
    private fun sendMessage(from: String, to: String, message: String) {
        listChats.clear()
        val data = hashMapOf<String, Any>(
            getString(R.string.message) to message,
            getString(R.string.from) to from,
            getString(R.string.to) to to
        )
        val lastTimeChat = hashMapOf(
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

    /**
     * Sets up the UI of the fragment
     */
    private fun setupUI() {
        listChats.clear()
        messageAdapter = MessageAdapter(requireContext(), listChats, currentUserId)
        binding.history.adapter = messageAdapter
        lifecycleScope.launch(Dispatchers.Main) {
            getAndDisplayMessages()
        }
    }

    /**
     * Adds the messages documents to the current list of messages.
     *
     * @param documents the list of documents we are fetching the messages from
     */
    private fun addMessagesToChatList(documents: MutableList<DocumentSnapshot>) {
        listChats.clear()
        for (document in documents) {
            val message = document.getString(getString(R.string.message))
            if (message != null) {
                listChats.add(
                    Chat(
                        document.getString(getString(R.string.from)),
                        document.getString(getString(R.string.to)),
                        message
                    )
                )
                messagesLiveData.postValue(listChats)
            }
        }
    }

    /**
     * Adds a change listener to the document where the last chat time is stored.
     */
    private fun setupMessageRefresh() {
        val lastTimeChatDocument = getUserDocument(currentUserId)
            .collection(getString(R.string.messagePartners)).document(partnerId)
        addOnChangeListener(lastTimeChatDocument)
    }

    /**
     * Adds a change listener to a document
     *
     * @param ref the document to add the listener to
     */
    private fun addOnChangeListener(ref: DocumentReference) {
        ref.addSnapshotListener { _, e ->
            if (e == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    getAndDisplayMessages()
                }
            }
        }
    }

    /**
     * Gets the messages and displays them with the help of the adapter
     */
    private suspend fun getAndDisplayMessages() {
        listChats.clear()
        val messages = firebaseFirestore.collection(getString(R.string.users))
            .document(currentUserId).collection(getString(R.string.chats))
            .document(partnerId).collection(getString(R.string.messages))
            .get().await()
        addMessagesToChatList(messages.documents)
        lifecycleScope.launch(Dispatchers.Main) {
            if (listChats.isNotEmpty()) {
                binding.history.scrollToPosition(listChats.size - 1)
            }
        }
    }
}