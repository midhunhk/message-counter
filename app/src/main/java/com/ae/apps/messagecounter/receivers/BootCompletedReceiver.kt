package com.ae.apps.messagecounter.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ae.apps.messagecounter.services.CounterServiceHelper
import org.jetbrains.anko.doAsync

/**
 * Broadcast receiver on restart of the device and start the background service if opted in
 */
class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        private const val SERVICE_START_DELAY = 1000L
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        doAsync {
            // waiting before starting the service to be fair to other processes on startup
            Thread.sleep(SERVICE_START_DELAY)

            CounterServiceHelper.monitorMessagesInBackground(context!!)
        }
    }
}