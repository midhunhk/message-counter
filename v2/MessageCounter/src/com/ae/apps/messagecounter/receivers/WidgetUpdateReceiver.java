/*
 * Copyright 2014 Midhun Harikumar
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

package com.ae.apps.messagecounter.receivers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.activities.MainActivity;
import com.ae.apps.messagecounter.managers.SentCountDataManager;
import com.ae.apps.messagecounter.vo.SentCountDetailsVo;

/**
 * Update the data for the widgets
 * 
 * @author Midhun
 * 
 */
public class WidgetUpdateReceiver extends AppWidgetProvider {

	private static final String	METHOD_SET_TEXT	= "setText";
	private static final String	TAG				= "WidgetUpdateReceiver";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		RemoteViews remoteView = null;
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		// Get data from database here
		SentCountDataManager dataManager = new SentCountDataManager();
		long start = System.currentTimeMillis();
		SentCountDetailsVo detailsVo = dataManager.getSentCountData(context);
		long diff = System.currentTimeMillis() - start;
		Log.d(TAG, diff + " ms");

		// update the app widgets
		for (int i = 0; i < appWidgetIds.length; i++) {
			remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			// update data here
			remoteView.setCharSequence(R.id.widgetSentTodayText, METHOD_SET_TEXT, "" + detailsVo.getSentToday());
			remoteView.setCharSequence(R.id.widgetSentInCycleText, METHOD_SET_TEXT, detailsVo.getSentCycle() + " / "
					+ detailsVo.getCycleLimit());

			remoteView.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteView);
		}
	}

}
