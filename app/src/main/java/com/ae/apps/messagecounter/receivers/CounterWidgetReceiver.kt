package com.ae.apps.messagecounter.receivers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.RemoteViews
import com.ae.apps.messagecounter.MainActivity
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModel


class CounterWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val METHOD_SET_TEXT = "setText"
        var remoteView: RemoteViews?
        val intent = Intent(context, MainActivity::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(context))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(context).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(context).ignoredNumbersDao())
        val viewModel = CounterViewModel(counterRepository, ignoreNumbersRepository, preferenceRepository)

        val sentDetails = viewModel.getSentCountDetails().value
        val sentToday = sentDetails?.sentToday ?: 0
        val sentCycle = sentDetails?.sentCycle ?: 0
        val cycleLimit = sentDetails?.cycleLimit ?: 0

        for (i in 0 until appWidgetIds.size) {
            remoteView = RemoteViews(context.packageName, R.layout.widget_layout)

            // update data here
            remoteView.setCharSequence(R.id.widgetSentTodayText, METHOD_SET_TEXT, sentToday.toString())
            remoteView.setCharSequence(R.id.widgetSentInCycleText, METHOD_SET_TEXT,
                    "$sentCycle / $cycleLimit")
            remoteView.setOnClickPendingIntent(R.id.appWidget, pendingIntent)

            appWidgetManager?.updateAppWidget(appWidgetIds[i], remoteView)
        }
    }

}