package com.ae.apps.messagecounter.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ae.apps.messagecounter.services.CounterServiceHelper

/**
 * Broadcast receiver for update of MessageCounter and restart the service if opted in
 */
class PackageReplacedReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val replacedPackage = intent?.dataString
        val currentPackage = context?.packageName

        if (null != replacedPackage && replacedPackage.contains(currentPackage!!)) {
            CounterServiceHelper.monitorMessagesInBackground(context)
        }
    }
}