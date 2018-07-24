package com.ae.apps.messagecounter.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.getMessageCounterServiceIntent

/**
 * Broadcast receiver for update of MessageCounter and restart the service if opted in
 */
class PackageReplacedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val replacedPackage = intent?.dataString
        val currentPackage = context?.packageName

        if (null != replacedPackage && replacedPackage.contains(currentPackage!!)) {
            val preferenceRepository = PreferenceRepository.newInstance(
                    PreferenceManager.getDefaultSharedPreferences(context))
            if(preferenceRepository.backgroundServiceEnabled()){
                context.startService(getMessageCounterServiceIntent(context))
            }
        }
    }
}