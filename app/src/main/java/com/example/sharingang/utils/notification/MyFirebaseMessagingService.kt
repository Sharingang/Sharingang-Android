package com.example.sharingang.utils.notification

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.example.sharingang.R
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.utils.constants.NotificationFields
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Class used to receive the push notifications from Firebase Message
 */
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            val deeplink = remoteMessage.data["deeplink"]
            val (title, recipient, channelId) = when (remoteMessage.data["notificationType"]) {
                NotificationFields.NEW_ITEM_TOPIC -> {
                    onNewItemTopic(remoteMessage)
                }
                NotificationFields.CHAT_TOPIC -> {
                    onChatTopic(remoteMessage)
                }
                else -> Triple("", null, "")
            }
            val isCorrectRecipient = when (channelId) {
                NotificationFields.NEW_ITEM_CHANNEL_ID -> recipient != currentUserProvider.getCurrentUserId()
                NotificationFields.CHAT_CHANNEL_ID -> recipient == currentUserProvider.getCurrentUserId()
                else -> false
            }
            if (recipient != null && isCorrectRecipient && deeplink != null) {
                remoteMessage.notification?.let {
                    sendNotification(it.body!!, title, channelId, deeplink)
                }
            }
        }
    }

    private fun onNewItemTopic(remoteMessage: RemoteMessage): Triple<String, String?, String> {
        return Triple(
            applicationContext.getString(R.string.new_item_notification_message),
            remoteMessage.data["userId"],
            NotificationFields.NEW_ITEM_CHANNEL_ID
        )
    }

    private fun onChatTopic(remoteMessage: RemoteMessage): Triple<String, String?, String> {
        return Triple(
            remoteMessage.data["fromName"] + ":",
            remoteMessage.data["toId"],
            NotificationFields.CHAT_CHANNEL_ID
        )
    }

    override fun onNewToken(token: String) {
        // Save the token if needed
    }

    private fun sendNotification(
        messageBody: String,
        messageTitle: String,
        channelId: String,
        deeplink: String
    ) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
            messageBody,
            messageTitle,
            channelId,
            deeplink,
            applicationContext
        )
    }
}
