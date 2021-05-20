package com.example.sharingang.database.firestore

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sharingang.models.User
import com.example.sharingang.database.store.UserStore
import com.example.sharingang.models.Chat
import com.example.sharingang.ui.fragments.MessageFragment
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "FirestoreUserStore"

/**
 * Implementation of UserRepository using the Firestore database
 *
 * During development it requires running the Firebase emulator (see README.md)
 */
@Singleton
class FirestoreUserStore @Inject constructor(private val firestore: FirebaseFirestore) :
    UserStore, AbstractFirestoreStore<User>(
    DatabaseFields.DBFIELD_USERS,
    User::class.java, firestore) {

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

    override suspend fun add(element: User): String? {
        requireNotNull(element.id)
        return if (super.update(element, element.id)) {
            element.id
        } else {
            null
        }
    }

    override suspend fun update(user: User): Boolean {
        requireNotNull(user.id)
        return super.update(user, user.id)
    }

    override suspend fun report(
        reportedUser: User,
        reporterUser: User,
        description: String,
        reason: String
    ): Boolean {
        return try {
            firestore
                .collection(DatabaseFields.DBFIELD_USERS).document(reportedUser.id!!)
                .collection(DatabaseFields.DBFIELD_REPORTS).document(reporterUser.id!!).set(
                    hashMapOf(
                        DatabaseFields.DBFIELD_REPORTER to reporterUser.id,
                        DatabaseFields.DBFIELD_REASON to reason,
                        DatabaseFields.DBFIELD_DESCRIPTION to description,
                        DatabaseFields.DBFIELD_REPORTEDAT to Date()
                    )
                )
            Log.d(TAG, "User ${reporterUser.id} reported ${reportedUser.id}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "User ${reporterUser.id} reported ${reportedUser.id}")
            false
        }
    }

    override suspend fun hasBeenReported(reporterId: String, reportedId: String): Boolean {
        val docIdRef = firestore.collection(DatabaseFields.DBFIELD_USERS).document(reportedId)
            .collection(DatabaseFields.DBFIELD_REPORTS).document(reporterId).get().await()
        return docIdRef.exists()
    }

    override suspend fun getChatPartners(userId: String): MutableList<String> {
        val partnerIds = mutableListOf<String>()
        val chatPartners = firestore.collection(DatabaseFields.DBFIELD_USERS)
            .document(userId).collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).get()
            .await()
        val listOfDocuments = chatPartners.documents
        listOfDocuments.forEach { partnerIds.add(it.id) }
        return partnerIds
    }

    override suspend fun getMessages(userId: String, with: String): MutableList<Chat> {
        val documents = firestore.collection(DatabaseFields.DBFIELD_USERS)
            .document(userId).collection(DatabaseFields.DBFIELD_CHATS)
            .document(with).collection(DatabaseFields.DBFIELD_MESSAGES)
            .get().await().documents
        val listChats = mutableListOf<Chat>()
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
            }
        }
        return listChats
    }

    override suspend fun putMessage(from: String, to: String, message: String): MutableList<Chat> {
        val data = hashMapOf<String, Any>(
            DatabaseFields.DBFIELD_MESSAGE to message,
            DatabaseFields.DBFIELD_FROM to from,
            DatabaseFields.DBFIELD_TO to to
        )
        val date = Date()
        val lastTimeChatCurrent = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date (${System.currentTimeMillis()})",
            DatabaseFields.DBFIELD_NUM_UNREAD to 0
        )
        val numUnread = getNumUnread(to, from)
        val lastTimeChatOther = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date(${System.currentTimeMillis()})",
            DatabaseFields.DBFIELD_NUM_UNREAD to numUnread + 1
        )
        getUserDocument(from).collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(to).set(lastTimeChatCurrent)
        getUserDocument(to).collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(from).set(lastTimeChatOther)

        val fromToPair = LinkedPair(from, to)
        listOf(from, to).forEach {
            val userDocument = getUserDocument(it)
            userDocument.collection(DatabaseFields.DBFIELD_CHATS).document(fromToPair.otherOf(it)!!)
                .collection(DatabaseFields.DBFIELD_MESSAGES).document(date.toString()).set(data)
        }
        return getMessages(from, to)
    }

    override suspend fun setupRefresh(
        userId: String, with: String, action: () -> Unit) {
        val ref = getUserDocument(userId)
            .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(with)
        ref.addSnapshotListener { _, e ->
            if (e == null) {
                action()
            }
        }
    }

    override suspend fun getNumUnread(userId: String, with: String): Long {
        val currentUserDocument = getUserDocument(userId)
        val numUnread = currentUserDocument.collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(with).get().await().getLong(DatabaseFields.DBFIELD_NUM_UNREAD)
        return numUnread ?: 0
    }

    /**
     * Gets the document corresponding to a particular user
     *
     * @param userId the user whose document we want to fetch
     * @return the fetched document
     */
    private fun getUserDocument(userId: String): DocumentReference {
        return firestore.collection(DatabaseFields.DBFIELD_USERS).document(userId)
    }
}
