package com.ae.apps.messagecounter.observers

import android.appwidget.AppWidgetManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.preference.PreferenceManager
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.business.MessageCounter
import com.ae.apps.messagecounter.data.business.getMessageFromCursor
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.content.ComponentName
import android.content.Intent


class SMSObserver(handler: Handler?, private val mContext: Context, private val mUriToObserve: Uri) : ContentObserver(handler), MessageCounter.MessageCounterObserver {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        val preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(mContext))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(mContext).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(mContext).ignoredNumbersDao())
        val messageCounter = MessageCounter.newInstance(counterRepository, ignoreNumbersRepository, preferenceRepository)

        val cursor = mContext.contentResolver.query(mUriToObserve, null, null, null, null);
        if (null != cursor && cursor.moveToNext()) {
            val message = getMessageFromCursor(cursor)
            cursor.close()

            // This observer maybe triggered multiple times for the same messageId
            val lastIndexedMessageId = preferenceRepository.getLastSentMessageId()

            // Index all messages only if a message was just sent and not already indexed
            if (message.protocol == null && message.id != lastIndexedMessageId) {
                // Index messages sent between last recorded sent message
                messageCounter.indexMessages(mContext, this)
            }
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

    private fun showMessageLimitNotification() {
        val preferenceRepository = PreferenceRepository.newInstance(
                PreferenceManager.getDefaultSharedPreferences(mContext))
        if(preferenceRepository.messageLimitNotificationEnabled()){

        }
    }

}