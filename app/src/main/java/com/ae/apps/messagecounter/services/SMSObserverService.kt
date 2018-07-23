package com.ae.apps.messagecounter.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import com.ae.apps.messagecounter.observers.SMSObserver
import com.ae.apps.common.managers.SMSManager


/**
 * Background service to register an Observer for SMS Content Provider
 */
class SMSObserverService : Service() {

    private var mObserver:SMSObserver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if(null == mObserver){
            val smsUri = Uri.parse(SMSManager.SMS_URI_ALL)
            mObserver = SMSObserver(Handler(), baseContext, smsUri)

            contentResolver.registerContentObserver(smsUri, true, mObserver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(null != mObserver){
            contentResolver.unregisterContentObserver(mObserver)
        }
    }

}