package com.example.sharingang.utils.notification

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.example.sharingang.R
import com.example.sharingang.auth.CurrentUserProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Class used to receive the push notifications from Firebase Message
 */
@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            val userId = remoteMessage.data["userId"]
            val deeplink = remoteMessage.data["deeplink"]
            val title = when (remoteMessage.data["notificationType"]) {
                "new_item" -> applicationContext.getString(R.string.new_item_notification_message)
                "chat" -> applicationContext.getString(R.string.chat_notification_message)
                else -> ""
            }
            val to = remoteMessage.data["toId"]
            if ((userId!=null && userId != currentUserProvider.getCurrentUserId() || (to!=null && to==currentUserProvider.getCurrentUserId())) && deeplink != null) {
                remoteMessage.notification?.let {
                    sendNotification(it.body!!, title, deeplink)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        // Save the token if needed
    }

    private fun sendNotification(messageBody: String, messageTitle:String, deeplink: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(messageBody, messageTitle, deeplink, applicationContext)
    }
}
