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
            var recipient: String? = null
            var isCorrectRecipient = false
            var channelId = ""
            val deeplink = remoteMessage.data["deeplink"]
            val title = when (remoteMessage.data["notificationType"]) {
                NotificationFields.NEW_ITEM_TOPIC -> {
                    recipient = remoteMessage.data["userId"]
                    isCorrectRecipient = recipient != currentUserProvider.getCurrentUserId()
                    channelId = NotificationFields.NEW_ITEM_CHANNEL_ID
                    applicationContext.getString(R.string.new_item_notification_message)
                }
                NotificationFields.CHAT_TOPIC -> {
                    recipient = remoteMessage.data["toId"]
                    isCorrectRecipient = recipient == currentUserProvider.getCurrentUserId()
                    channelId = NotificationFields.CHAT_CHANNEL_ID
                    remoteMessage.data["fromName"] + ":"
                }
                else -> ""
            }
            if (recipient != null && isCorrectRecipient && deeplink != null) {
                remoteMessage.notification?.let {
                    sendNotification(it.body!!, title, channelId, deeplink)
                }
            }
        }
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
