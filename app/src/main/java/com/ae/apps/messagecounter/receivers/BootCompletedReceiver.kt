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