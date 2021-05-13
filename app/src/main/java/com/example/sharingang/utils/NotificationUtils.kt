package com.example.sharingang.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sharingang.MainActivity
import com.example.sharingang.R
import com.google.firebase.messaging.FirebaseMessaging

private const val NOTIFICATION_ID = 0

/**
 * Send and display a notification
 * @param messageBody the message to be displayed in the notification
 * @param applicationContext the context of the application
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = buildNotification(messageBody, applicationContext, contentPendingIntent)
    notify(NOTIFICATION_ID, builder.build())
}

private fun buildNotification(
    messageBody: String,
    applicationContext: Context,
    contentPendingIntent: PendingIntent
): NotificationCompat.Builder {
    return NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.new_item_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(
            applicationContext.getString(R.string.new_item_notification_message)
        )
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setAutoCancel(true)
}

/**
 * Create a notification channel, required since Android O
 * @param channelId the id of the channel
 * @param channelName the name of the channel
 * @param activity the activity
 */
fun createChannel(channelId: String, channelName: String, activity: Activity) {
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
        notificationChannel.description = activity.getString(R.string.new_item_notification_channel_description)
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
fun unsubscribeFromTopic(topic:String) {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
}