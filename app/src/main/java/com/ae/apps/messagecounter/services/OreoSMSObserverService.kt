package com.ae.apps.messagecounter.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.ae.apps.messagecounter.R

class OreoSMSObserverService : SMSObserverService() {

    private val CHANNEL_ID = "MessageCounterService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
        }

        val resources = applicationContext.resources
        val notificationTitle = resources.getString(R.string.app_name)
        val notificationText = resources.getString(R.string.str_notification_foreground_service)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .build()
        startForeground((System.currentTimeMillis() and 0xfffffffL).toInt(), notification)
    }
}