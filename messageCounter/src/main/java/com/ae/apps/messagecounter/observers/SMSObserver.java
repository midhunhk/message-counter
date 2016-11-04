package com.ae.apps.messagecounter.observers;

import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.activities.MainActivity;
import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.receivers.WidgetUpdateReceiver;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

/**
 * This class will observe the a content provider for changes
 * 
 * @author Midhun
 * 
 */
public class SMSObserver extends ContentObserver {

	private static final String	COLUMN_NAME_PROTOCOL	= "protocol";
	private static final String	COLUMN_NAME_ID			= "_id";

	private Uri					observableUri			= null;
	private Context				mContext				= null;

	public SMSObserver(Handler handler, Context context, Uri uriToObserve) {
		super(handler);
		mContext = context;
		observableUri = uriToObserve;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = mContext.getContentResolver().query(observableUri, null, null, null, null);
		if (cursor.moveToNext()) {
			// We need the protocol and the message _id
			String messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
			String protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL));

			// Read lastMessageId from SharedPreferences
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext); 
			String lastMessageId = sharedPreferences.getString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, "");
			
			// See if this message was processed earlier, sometimes same messageId can come multiple times
			boolean isNewMessage = !lastMessageId.equals(messageId);

			// protocol will be null for sent messages
			if (protocol == null && isNewMessage) {
				// A Message was sent just now
				Date date = Calendar.getInstance().getTime();

				// Lets open a database connection and add an entry
				CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);
				long today = MessageCounterUtils.getIndexFromDate(date);

				// Add an entry into the database
				counterDataBase.addMessageSentCounter(today);

				// if sentcount limit and notify on reching the limit are enabled, we shall show a notification
				showMessageLimitNotification(counterDataBase);

				// Close the connection to the database
				counterDataBase.close();

				// Store this message id incase we get multiple callbacks for the same id
				Log.d("SendSMSObserver", " An SMS was sent at " + date.getTime() + " with id " + messageId);
				sharedPreferences
					.edit()
					.putString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, messageId)
					.commit();

				// Send a broadcast to update our widgets
				sendWidgetUpdateBroadcast();
			}
		}
		cursor.close();
	}

	private void showMessageLimitNotification(CounterDataBaseAdapter counterDataBase) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean sentCountlimitEnabled = preferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);
		boolean notifEnabled = preferences.getBoolean(AppConstants.PREF_KEY_ENABLE_NOTIFICATION, false);
		if (sentCountlimitEnabled && notifEnabled) {
			Date currentCycleStartDate = MessageCounterUtils.getCycleStartDate(preferences);
			long dateIndex = MessageCounterUtils.getIndexFromDate(currentCycleStartDate);
			int userLimit = MessageCounterUtils.getMessageLimitValue(preferences);
			int currentCount = counterDataBase.getTotalSentCountSinceDate(dateIndex);
			if (currentCount == userLimit) {
				// Get some resources for the notification
				Resources resources = mContext.getResources();
				String notificationTitle = resources.getString(R.string.str_sms_limit_notif_title);
				String notificationText = resources.getString(R.string.str_sms_limit_notif_text);

				// Crete the intent for running this app when user clicks on the notification
				Intent resultIntent = new Intent(mContext, MainActivity.class);
				PendingIntent resultPendingIntent = PendingIntent
						.getActivity(mContext, AppConstants.NOTIFICATION_REQUEST_CODE, resultIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notification = new NotificationCompat.Builder(mContext)
						.setContentIntent(resultPendingIntent)
						.setContentTitle(notificationTitle)
						.setContentText(notificationText)
						.setNumber(userLimit)
						.setSmallIcon(R.drawable.ic_app_icon)
						.setAutoCancel(true)
						.build();

				// Get an instance of the notification manager service
				NotificationManager notificationManager = (NotificationManager) mContext
						.getSystemService(Context.NOTIFICATION_SERVICE);

				// Show a notification to the user here "send message for this cycle has reached limit"
				notificationManager.notify(0, notification);
			}
		}
	}

	private void sendWidgetUpdateBroadcast() {
		// We try to send a broadcast to trigger the widget update call
		Intent intent = new Intent(mContext, WidgetUpdateReceiver.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		// Get all the widgetIds
		int appWidgetIds[] = AppWidgetManager.getInstance(mContext).getAppWidgetIds(
				new ComponentName(mContext, WidgetUpdateReceiver.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		// Finally send the broadcast through the system
		mContext.sendBroadcast(intent);
	}

}
