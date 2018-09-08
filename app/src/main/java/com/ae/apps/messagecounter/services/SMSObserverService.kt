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

package com.ae.apps.messagecounter.services

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.observers.SMSObserver

/**
 * Background service to register an Observer for SMS Content Provider
 */
open class SMSObserverService : Service() {

    private var mObserver: ContentObserver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (null == mObserver) {
            val smsUri = Uri.parse(SMSManager.SMS_URI_ALL)
            mObserver = SMSObserver(Handler(), baseContext)

            contentResolver.registerContentObserver(smsUri, true, mObserver!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mObserver) {
            contentResolver.unregisterContentObserver(mObserver!!)
        }
    }

}