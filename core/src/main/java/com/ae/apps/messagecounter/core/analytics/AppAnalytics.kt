/*
 * Copyright 2019 Midhun Harikumar
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
package com.ae.apps.messagecounter.core.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.ae.apps.messagecounter.core.R
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

/**
 * Provides setup code for FirebaseAnalytics
 */
class AppAnalytics() {

    companion object {
        private const val VIEW_ITEM = "view_item"
        private const val APP_START = "app_start"
        private const val SCREEN_NAME = "screen_name"

        private lateinit var firebaseAnalytics: FirebaseAnalytics
        private lateinit var paramStartTime:String
        private lateinit var paramAppName:String

        @SuppressLint("MissingPermission")
        fun newInstance(context: Context): AppAnalytics {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val resources = context.resources
            paramStartTime = resources.getString(R.string.analytics_key_start_time)
            paramAppName = resources.getString(R.string.analytics_key_app_name)
            return AppAnalytics()
        }
    }

    fun logEvent(eventType: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventType, bundle)
    }

    fun logEvent(eventType: String, paramName: String, paramValue: String) {
        val bundle = Bundle()
        bundle.putString(paramName, paramValue)
        logEvent(eventType, bundle)
    }

    fun logAppStart(appName: String) {
        val bundle = Bundle()
        bundle.putString(paramAppName, appName)
        bundle.putString(paramStartTime,Calendar.getInstance().time.toString())
        logEvent(APP_START, bundle)
    }

    fun logScreenView(screenName: String) {
        logEvent(VIEW_ITEM, SCREEN_NAME, screenName)
    }
}