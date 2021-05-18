package com.example.sharingang.ui.fragments

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Database
import com.bumptech.glide.Glide
import com.example.sharingang.models.Chat
import com.example.sharingang.ui.adapters.MessageAdapter
import com.example.sharingang.databinding.FragmentMessageBinding
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


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
    private lateinit var currentUserDocRef: DocumentReference
    private lateinit var partnerUserDocRef: DocumentReference

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

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
        partnerId = args.partnerId
        partnerUsername = args.partnerUsername
        partnerProfilePic = args.partnerProfilePictureUrl
        currentUserId = currentUserProvider.getCurrentUserId()!!
        currentUserDocRef = getUserDocument(currentUserId)
        partnerUserDocRef = getUserDocument(partnerId)
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
            val message = binding.messageEditText.text.toString()
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
        val usersCollection = firebaseFirestore.collection(DatabaseFields.DBFIELD_USERS)
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
        val messageData = generateMsgData(from, to, message)
        val date = Date()
        var currentNumUnread: Long?
        lifecycleScope.launch(Dispatchers.Main) {
            // get the current number of unread messages from the database
            currentNumUnread = getUserDocument(partnerId)
                .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(currentUserId)
                .get().await().getLong(DatabaseFields.DBFIELD_NUM_UNREAD)
            // if the document does not exist, we have a new partner -> the number of unread
            // messages is then 1.
            val nextNumUnread = if(currentNumUnread == null) 1 else currentNumUnread!! + 1
            val chatMaps = generateUnreadData(date, nextNumUnread)
            val lastTimeChatCurrent = chatMaps.first
            val lastTimeChatPartner = chatMaps.second
            val fromToPair = LinkedPair(from, to)
            updateLastChat(currentUserId, partnerId, lastTimeChatCurrent)
            updateLastChat(partnerId, currentUserId, lastTimeChatPartner)
            listOf(from, to).forEach {
                val userDocument = getUserDocument(it)
                userDocument.collection(DatabaseFields.DBFIELD_CHATS).document(fromToPair.otherOf(it)!!)
                    .collection(DatabaseFields.DBFIELD_MESSAGES).document(date.toString()).set(messageData)
            }
            binding.messageEditText.text.clear()
        }
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
            val message = document.getString(DatabaseFields.DBFIELD_MESSAGE)
            if (message != null) {
                listChats.add(
                    Chat(
                        document.getString(DatabaseFields.DBFIELD_FROM),
                        document.getString(DatabaseFields.DBFIELD_TO),
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
            .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(partnerId)
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
                    if(isAdded) {
                        getAndDisplayMessages()
                        clearNumUnread(currentUserDocRef)
                    }
                }
            }
        }
    }

    /**
     * Gets the messages and displays them with the help of the adapter
     */
    private suspend fun getAndDisplayMessages() {
        listChats.clear()
        val messages = firebaseFirestore.collection(DatabaseFields.DBFIELD_USERS)
            .document(currentUserId).collection(DatabaseFields.DBFIELD_CHATS)
            .document(partnerId).collection(DatabaseFields.DBFIELD_MESSAGES)
            .get().await()
        addMessagesToChatList(messages.documents)
        lifecycleScope.launch(Dispatchers.Main) {
            if (listChats.isNotEmpty()) {
                binding.history.scrollToPosition(listChats.size - 1)
                clearNumUnread(currentUserDocRef)
            }
        }
    }

    /**
     * Generates the data of a message document
     *
     * @param from the sender
     * @param to the receiver
     * @param msg the message
     * @return
     */
    private fun generateMsgData(from: String, to: String, msg: String): HashMap<String, String> {
        return (
            hashMapOf (
                DatabaseFields.DBFIELD_MESSAGE to msg,
                DatabaseFields.DBFIELD_FROM to from,
                DatabaseFields.DBFIELD_TO to to
            )
        )
    }

    /**
     * Generates the data for unread messages
     *
     * @param date the date
     * @param updatedUnread the number of unread messages
     * @return the generated data for both users as a Pair(current, partner)
     */
    private fun generateUnreadData(date: Date, updatedUnread: Long):
            Pair<HashMap<String, out Any>, HashMap<String, out Any>> {
        val lastTimeChatCurrent = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date (${System.currentTimeMillis()})",
            DatabaseFields.DBFIELD_NUM_UNREAD to 0
        )

        val lastTimeChatPartner = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date (${System.currentTimeMillis()})",
            DatabaseFields.DBFIELD_NUM_UNREAD to updatedUnread
        )
        return Pair(lastTimeChatCurrent, lastTimeChatPartner)
    }

    /**
     * Updates the last time two users have chatted
     *
     * @param rootUser the user whose data we need to update
     * @param childUser the user with whom the rootUser's data we need to update
     * @param data the new data
     */
    private fun updateLastChat(rootUser: String, childUser: String, data: HashMap<String, out Any>) {
        firebaseFirestore.collection(DatabaseFields.DBFIELD_USERS).document(rootUser)
            .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(childUser)
            .set(data)
    }

    /**
     * Clears the number of unread messages for a particular document
     *
     * @param documentReference the document's number of unread messages to clear
     */
    private fun clearNumUnread(documentReference: DocumentReference) {
        documentReference.collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(partnerId).update(DatabaseFields.DBFIELD_NUM_UNREAD, 0)
    }
}
