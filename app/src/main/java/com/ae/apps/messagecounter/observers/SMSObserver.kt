package com.ae.apps.messagecounter.observers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import com.ae.apps.messagecounter.MainActivity
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.MessageCounter
import com.ae.apps.messagecounter.getWidgetUpdateIntent
import java.util.*


/**
 * An observer that observes changes to the SMS Database
 */
class SMSObserver(handler: Handler?, private val mContext: Context) : ContentObserver(handler), MessageCounter.MessageCounterObserver {

    companion object {
        private const val CHANNEL_ID = "message_counter"
        private const val NOTIFICATION_REQUEST_CODE = 1042
        private const val SEND_COUNT_REACHED_ID = 0
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        val messageCounter = MessageCounter.newInstance(mContext)
        if (messageCounter.checkIfUnIndexedMessagesExist(mContext)) {
            messageCounter.indexMessages(mContext, this)
        }
    }

    override fun onIndexCompleted() {
        showMessageLimitNotification()
        sendWidgetUpdateBroadcast()
    }

    private fun sendWidgetUpdateBroadcast() {
        // Finally send the broadcast through the system
        mContext.sendBroadcast( getWidgetUpdateIntent(mContext) )
    }

    private fun showMessageLimitNotification() {
        val messageCounter = MessageCounter.newInstance(mContext)
        if (messageCounter.checkIfMessageLimitCrossed()) {

            val resources = mContext.resources
            val notificationTitle = resources.getString(R.string.str_sms_limit_notification_title)
            val notificationText = resources.getString(R.string.str_sms_limit_notification_text)

            // Crete the intent for running this app when user clicks on the notification
            val resultIntent = Intent(mContext, MainActivity::class.java)
            val resultPendingIntent = PendingIntent
                    .getActivity(mContext, NOTIFICATION_REQUEST_CODE, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)

            // Get an instance of the notification manager service
            val notificationManager = mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Android O and up must use a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setWhen(Calendar.getInstance().timeInMillis)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setAutoCancel(true)
                    .build()

            // Show a notification to the user here "send message for this cycle has reached limit"
            notificationManager.notify(SEND_COUNT_REACHED_ID, notification)
        }
    }

}