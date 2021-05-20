package com.example.sharingang.database.firestore

import android.util.Log
import com.example.sharingang.models.User
import com.example.sharingang.database.store.UserStore
import com.example.sharingang.models.Chat
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap


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
    User::class.java, firestore
) {

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

    override suspend fun getChatPartners(userId: String): List<String> {
        val chatPartners = firestore.collection(DatabaseFields.DBFIELD_USERS)
            .document(userId).collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).get()
            .await()
        return chatPartners.documents.map { it.id }
    }

    override suspend fun getMessages(userId: String, with: String): List<Chat> {
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

    override suspend fun putMessage(from: String, to: String, message: String): List<Chat> {
        val date = Date()
        val dataMaps = generateDataMaps(from, to, message, date)
        val data = dataMaps.first
        val lastTimeChatCurrent = dataMaps.second
        val lastTimeChatOther = dataMaps.third

        setLastTimeChatData(from, to, lastTimeChatCurrent)
        setLastTimeChatData(to, from, lastTimeChatOther)

        val fromToPair = LinkedPair(from, to)
        listOf(from, to).forEach {
            val userDocument = getUserDocument(it)
            userDocument.collection(DatabaseFields.DBFIELD_CHATS).document(fromToPair.otherOf(it)!!)
                .collection(DatabaseFields.DBFIELD_MESSAGES).document(date.toString()).set(data)
        }
        return getMessages(from, to)
    }

    override suspend fun setupRefresh(
        userId: String, with: String, action: () -> Unit
    ) {
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

    override suspend fun clearNumUnread(userId: String, with: String) {
        val currentUserDocument = getUserDocument(userId)
        currentUserDocument.collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(with).update(DatabaseFields.DBFIELD_NUM_UNREAD, 0)
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

    /**
     * Updates the data of the last chatted time for a user with their partner
     *
     * @param root the user whose data we need to update
     * @param child the user with whom we want to update the data
     * @param data the new data
     */
    private fun setLastTimeChatData(root: String, child: String, data: HashMap<String, out Any>) {
        getUserDocument(root).collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
            .document(child).set(data)
    }

    /**
     * Generate the data maps needed for user chat interaction (metadata + message)
     *
     * @param from the sender
     * @param to the receiver
     * @param message the message
     * @param date the current date
     * @return the generated maps
     */
    private suspend fun generateDataMaps(from: String, to: String, message: String, date: Date):
            Triple<HashMap<String, out Any>, HashMap<String, out Any>, HashMap<String, out Any>> {
        val data = hashMapOf<String, Any>(
            DatabaseFields.DBFIELD_MESSAGE to message,
            DatabaseFields.DBFIELD_FROM to from,
            DatabaseFields.DBFIELD_TO to to
        )
        val now = System.currentTimeMillis()
        val lastTimeChatCurrent = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date ($now)",
            DatabaseFields.DBFIELD_NUM_UNREAD to 0
        )
        val numUnread = getNumUnread(to, from)
        val lastTimeChatOther = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date ($now)",
            DatabaseFields.DBFIELD_NUM_UNREAD to numUnread + 1
        )
        return Triple(data, lastTimeChatCurrent, lastTimeChatOther)
    }
}
