package com.ae.apps.messagecounter.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.getMessageCounterServiceIntent
import org.jetbrains.anko.doAsync

/**
 * Broadcast receiver on restart of the device and start the background service if opted in
 */
class BootCompletedReceiver : BroadcastReceiver() {

    val SERVICE_START_DELAY = 1000L

    override fun onReceive(context: Context?, intent: Intent?) {
        doAsync {
            // waiting before starting the service to be fair to other processes on startup
            Thread.sleep(SERVICE_START_DELAY)

            val preferenceRepository = PreferenceRepository.newInstance(
                    PreferenceManager.getDefaultSharedPreferences(context))
            if(preferenceRepository.backgroundServiceEnabled()){
                context?.startService(getMessageCounterServiceIntent(context))
            }
        }
    }
}