package com.example.sharingang.utils.notification

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sharingang.BuildConfig
import com.example.sharingang.R
import com.google.firebase.messaging.FirebaseMessaging

private const val NOTIFICATION_ID = 0

/**
 * Send and display a notification
 * @param messageBody the message to be displayed in the notification
 * @param messageTitle the title of the message to be displayed
 * @param channelId the identifier of the notification channel
 * @param applicationContext the context of the application
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    messageTitle: String,
    channelId: String,
    deeplink: String,
    applicationContext: Context
) {
    val contentIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
    contentIntent.setPackage(BuildConfig.APPLICATION_ID)
    contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )
    val builder = buildNotification(
        messageBody,
        messageTitle,
        channelId,
        applicationContext,
        contentPendingIntent
    )
    notify(NOTIFICATION_ID, builder.build())
}

private fun buildNotification(
    messageBody: String,
    messageTitle: String,
    channelId: String,
    applicationContext: Context,
    contentPendingIntent: PendingIntent
): NotificationCompat.Builder {
    return NotificationCompat.Builder(
        applicationContext,
        channelId
    ).setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setAutoCancel(true)
}

/**
 * Create a notification channel, required since Android O
 * @param channelId the id of the channel
 * @param channelName the name of the channel
 * @param channelDescription the description of the channel
 * @param activity the activity
 */
fun createChannel(
    channelId: String,
    channelName: String,
    channelDescription: String,
    activity: Activity
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription
        val notificationManager = activity.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

/**
 * Subscribe to the topic on FirebaseMessaging
 * @param topic the topic to subscribe to
 */
fun subscribeToTopic(topic: String) {
    FirebaseMessaging.getInstance().subscribeToTopic(topic)
}

/**
 * Unsubscribe from the topic on FirebaseMessaging
 * @param topic the topic to unsubscribe from
 */
fun unsubscribeFromTopic(topic: String) {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
}
