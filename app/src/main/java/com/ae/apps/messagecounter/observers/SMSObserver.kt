package com.ae.apps.messagecounter.observers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import com.ae.apps.messagecounter.data.business.MessageCounter

/**
 * An observer that observes changes to the SMS Database
 */
class SMSObserver(handler: Handler?, private val mContext: Context) : ContentObserver(handler), MessageCounter.MessageCounterObserver {

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

    private fun showMessageLimitNotification() {
        if (mMessageCounter.checkIfMessageLimitCrossed()) {
            // TODO
        }
    }

}