package com.ae.apps.messagecounter.observers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.support.v4.app.NotificationCompat
import com.ae.apps.messagecounter.MainActivity
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.MessageCounter
import java.util.*


/**
 * An observer that observes changes to the SMS Database
 */
class SMSObserver(handler: Handler?, private val mContext: Context) : ContentObserver(handler), MessageCounter.MessageCounterObserver {

    private val NOTIFICATION_REQUEST_CODE = 1042
    private val SEND_COUNT_REACHED_ID = 0

    private lateinit var mMessageCounter: MessageCounter

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        mMessageCounter = MessageCounter.newInstance(mContext)
        if (mMessageCounter.checkIfUnIndexedMessagesExist(mContext)) {
            mMessageCounter.indexMessages(mContext, this)
        }
    }

    override fun onIndexCompleted() {
        showMessageLimitNotification()
        sendWidgetUpdateBroadcast()
    }

    private fun sendWidgetUpdateBroadcast() {
        // We try to send a broadcast to trigger the widget update call
        /* val intent = Intent(mContext, WidgetUpdateReceiver::class.java)
         intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
         // Get all the widgetIds
         val appWidgetIds = AppWidgetManager.getInstance(mContext).getAppWidgetIds(
                 ComponentName(mContext, WidgetUpdateReceiver::class.java))
         intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

         // Finally send the broadcast through the system
         mContext.sendBroadcast(intent)*/
    }

    @SuppressWarnings("deprecation")
    private fun showMessageLimitNotification() {
        if (mMessageCounter.checkIfMessageLimitCrossed()) {
            val resources = mContext.resources
            val notificationTitle = resources.getString(R.string.str_sms_limit_notification_title)
            val notificationText = resources.getString(R.string.str_sms_limit_notification_text)

            // Crete the intent for running this app when user clicks on the notification
            val resultIntent = Intent(mContext, MainActivity::class.java)
            val resultPendingIntent = PendingIntent
                    .getActivity(mContext, NOTIFICATION_REQUEST_CODE, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(mContext)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setWhen(Calendar.getInstance().timeInMillis)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setAutoCancel(true)
                    .build()

            // Get an instance of the notification manager service
            val notificationManager = mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Show a notification to the user here "send message for this cycle has reached limit"
            notificationManager.notify(SEND_COUNT_REACHED_ID, notification)
        }
    }

}