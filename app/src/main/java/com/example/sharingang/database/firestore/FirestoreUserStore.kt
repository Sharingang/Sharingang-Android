package com.example.sharingang.database.firestore

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
        val lastTimeChat = hashMapOf(
            DatabaseFields.DBFIELD_LAST_MESSAGE to "$date (${System.currentTimeMillis()})"
        )
        val fromToPair = LinkedPair(from, to)
        listOf(from, to).forEach {
            val userDocument = getUserDocument(it)
            userDocument.collection(DatabaseFields.DBFIELD_CHATS).document(fromToPair.otherOf(it)!!)
                .collection(DatabaseFields.DBFIELD_MESSAGES).document(date.toString()).set(data)
            userDocument.collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS)
                .document(fromToPair.otherOf(it)!!).set(lastTimeChat)
        }
        return getMessages(from, to)
    }

    override suspend fun setupRefresh(
        userId: String, with: String, action: () -> Unit, lifecycleScope: LifecycleCoroutineScope) {
        val ref = getUserDocument(userId)
            .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(with)
        ref.addSnapshotListener { _, e ->
            if (e == null) {
                action()
            }
        }
    }

    private fun getUserDocument(userId: String): DocumentReference {
        return firestore.collection(DatabaseFields.DBFIELD_USERS).document(userId)
    }
}
