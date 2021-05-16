package com.example.sharingang.utils.notification

import android.app.NotificationManager
import androidx.core.content.ContextCompat
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
            if (userId != currentUserProvider.getCurrentUserId()) {
                remoteMessage.notification?.let {
                    sendNotification(it.body!!)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        // Save the token if needed
    }

    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }
}
