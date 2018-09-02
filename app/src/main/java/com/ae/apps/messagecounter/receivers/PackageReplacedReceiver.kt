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