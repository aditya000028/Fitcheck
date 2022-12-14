package com.cmpt362.fitcheck.ui.settings.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.cmpt362.fitcheck.Util
import kotlin.math.abs

object NotificationHandler {

    private const val RECURRING_NOTIFICATION_REQUEST_CODE = 10

    fun changeOrStartNotification(context: Context, newTimeInMilli: Long) {
        cancelRecurringNotification(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        val pendingNotificationIntent = PendingIntent.getBroadcast(
            context,
            RECURRING_NOTIFICATION_REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentTimeInMilli = System.currentTimeMillis()
        val differenceInMilli = newTimeInMilli - currentTimeInMilli
        val timeToNotify: Long = if (differenceInMilli < 0) {
            currentTimeInMilli + (Util.MILLISECONDS_IN_A_DAY - abs(differenceInMilli))
        } else {
            newTimeInMilli
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeToNotify,
            Util.MILLISECONDS_IN_A_DAY,
            pendingNotificationIntent
        )
    }

    fun cancelRecurringNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RECURRING_NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}