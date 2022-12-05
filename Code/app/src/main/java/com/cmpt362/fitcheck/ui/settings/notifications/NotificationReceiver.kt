package com.cmpt362.fitcheck.ui.settings.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cmpt362.fitcheck.MainActivity
import com.cmpt362.fitcheck.R

/**
 * Responsible for receiving broadcast and displaying notification to user
 *
 * Reference: https://betterprogramming.pub/scheduled-notifications-in-android-2055356fb4f5
 */
class NotificationReceiver: BroadcastReceiver() {

    private val CHANNEL_ID = "RECURRING_NOTIFICATION"
    private val CHANNEL_NAME = "FITCHECK_CHANNEL"

    override fun onReceive(context: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT > 26) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.daily_notification_title))
            .setContentText(context.getString(R.string.daily_notification_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}