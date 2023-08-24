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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ae.apps.messagecounter.MainActivity
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.MessageCounter
import org.jetbrains.anko.doAsync

/**
 * Update the widget
 */
class CounterWidgetReceiver : AppWidgetProvider() {

    companion object {
        const val METHOD_SET_TEXT = "setText"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val intent = Intent(context, MainActivity::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val messageCounter = MessageCounter.newInstance(context)

        doAsync {
            var remoteView: RemoteViews?
            val sentDetails = messageCounter.getSentCountDetailsForWidget()
            val sentCycle = sentDetails.sentCycle
            val cycleLimit = sentDetails.cycleLimit

            for (element in appWidgetIds) {
                remoteView = RemoteViews(context.packageName, R.layout.widget_layout)

                // update data here
                remoteView.setCharSequence(R.id.widgetSentTodayText, METHOD_SET_TEXT, sentDetails.sentToday.toString())
                remoteView.setCharSequence(R.id.widgetSentInCycleText, METHOD_SET_TEXT, "$sentCycle / $cycleLimit")
                remoteView.setOnClickPendingIntent(R.id.appWidget, pendingIntent)

                appWidgetManager?.updateAppWidget(element, remoteView)
            }
        }


        /*
        val preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(context))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(context).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(context).ignoredNumbersDao())
        val viewModel = CounterViewModel(counterRepository, ignoreNumbersRepository, preferenceRepository)
*/

        //val sentDetails = viewModel.getSentCountDetails().value

    }

}