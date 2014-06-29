package com.ae.apps.messagecounter.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.activities.MainActivity;

public class SentCountWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		int appWidgetId = 0;
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		RemoteViews remoteView = null;

		for (int i = 0; i < appWidgetIds.length; i++) {
			appWidgetId = appWidgetIds[i];
			remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			remoteView.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

			// TODOD update

			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		}
	}

}
