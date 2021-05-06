package com.example.sharingang.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.sharingang.MainActivity
import com.example.sharingang.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = buildNotification(applicationContext, contentPendingIntent)
    notify(NOTIFICATION_ID, builder.build())
}

private fun buildNotification(
    applicationContext: Context,
    contentPendingIntent: PendingIntent
): NotificationCompat.Builder {
    return NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.new_item_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(
            applicationContext.getString(
                R.string.new_item_notification_message,
                "test"
            )
        )
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setAutoCancel(true)
}