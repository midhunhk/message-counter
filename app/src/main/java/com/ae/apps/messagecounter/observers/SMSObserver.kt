/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.messagecounter.observers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.MessageCounter
import com.ae.apps.messagecounter.getStartActivityIntent
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
        mContext.sendBroadcast( getWidgetUpdateIntent(mContext) )
    }

    private fun showMessageLimitNotification() {
        val messageCounter = MessageCounter.newInstance(mContext)
        if (messageCounter.checkIfMessageLimitCrossed()) {

            val resources = mContext.resources
            val notificationTitle = resources.getString(R.string.str_sms_limit_notification_title)
            val notificationText = resources.getString(R.string.str_sms_limit_notification_text)

            val notificationManager = mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Android O and up must use a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            // An intent for launching this app when user clicks on the notification
            val resultPendingIntent = getStartActivityIntent(mContext, NOTIFICATION_REQUEST_CODE)

            val notification = NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setWhen(Calendar.getInstance().timeInMillis)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setAutoCancel(true)
                    .build()

            // Show a notification to the like "send message for this cycle has reached limit"
            notificationManager.notify(SEND_COUNT_REACHED_ID, notification)
        }
    }

}