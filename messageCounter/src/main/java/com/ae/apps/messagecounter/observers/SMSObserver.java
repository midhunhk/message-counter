/*
 * Copyright 2016 Midhun Harikumar
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

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.activities.MainActivity;
import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.receivers.WidgetUpdateReceiver;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

/**
 * SMSObserver will observe the SMS content provider for any changes
 * 
 * @author Midhun
 * 
 */
public class SMSObserver extends ContentObserver {

    // Class Constants
	private static final String	COLUMN_NAME_PROTOCOL	= "protocol";
	private static final String	COLUMN_NAME_ID			= "_id";
	private static final String COLUMN_NAME_DATE		= "date";
	private static final String TAG 					= "SMSObserver";

    // Private references that are set from the ctor
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
		if (null != cursor && cursor.moveToNext()) {
			// We need the protocol and the message _id
			String messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
			String protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL));

			// Read lastMessageId from SharedPreferences
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext); 
			String lastMessageId = sharedPreferences.getString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, "");

			// See if this message was processed earlier, sometimes same messageId can come multiple times
			boolean isNewMessage = !lastMessageId.equals(messageId);

			// Experimental feature - For Unicorn
			// Check for any messages that we might have missed since the last message was logged
            // If new message id is different from last saved message id, check if any messages
            // went un counted. Enable this flag from the preferences menu
			if((isNewMessage || true) && PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getBoolean(AppConstants.PREF_KEY_ENABLE_OFFLINE_COUNT, false)) {
				checkForMessagesNotLogged(lastMessageId, messageId);
			}

			// protocol will be null for sent messages
			if (protocol == null && isNewMessage) {
				// A Message was sent just now
				Date date = Calendar.getInstance().getTime();

				// Lets open a database connection and add an entry
				CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);
				long today = MessageCounterUtils.getIndexFromDate(date);

				// Add an entry into the database
				counterDataBase.addMessageSentCounter(today);

				// if sent count limit and notify on reaching the limit are enabled, we shall show a notification
				showMessageLimitNotification(counterDataBase);

				// Close the connection to the database
				counterDataBase.close();

				// Store this message id in case we get multiple callbacks for the same id
				Log.d("SendSMSObserver", " An SMS was sent at " + date.getTime() + " with id " + messageId);
				sharedPreferences
					.edit()
					.putString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, messageId)
					.apply();

				// Send a broadcast to update our widgets
				sendWidgetUpdateBroadcast();
			}
		}
		if(null != cursor) {
			cursor.close();
		}
	}

    /**
     * Check for sent messages that were not tracked by the background service
     *
     * @param lastMessageId the last message id
     * @param messageId the new message id
     */
	private void checkForMessagesNotLogged(String lastMessageId, String messageId) {
		// Get the timestamp for the lastMessage that was logged
		String[] projection = new String[]{ COLUMN_NAME_ID, COLUMN_NAME_DATE };
		Cursor lastMessageCursor = mContext.getContentResolver().query(
                    observableUri,
				    projection,
				    COLUMN_NAME_ID + "= ? ",
				    new String[]{ lastMessageId }, null);

		int lastMessageCount = (null != lastMessageCursor) ? lastMessageCursor.getCount() : 0;

		Log.d(TAG, "lastMessageCursor count :" + lastMessageCount + " Id : " + lastMessageId);

        if(null != lastMessageCursor && lastMessageCursor.moveToFirst()) {
            // Get the timestamp for the last sent message that was logged
            Long lastMessageTimeStamp = Long.parseLong(lastMessageCursor.getString(
                    lastMessageCursor.getColumnIndex(COLUMN_NAME_DATE)));
            lastMessageCursor.close();

            Log.d(TAG, "lastMessageTimeStamp :" + lastMessageTimeStamp);

            Cursor newMessagesCursor = mContext.getContentResolver().query(
                    Uri.parse(SMSManager.SMS_URI_SENT),
                    projection,
                    COLUMN_NAME_DATE + "> ? ",
                    new String[]{ String.valueOf(lastMessageTimeStamp) }, null);

			int newMessagesCount = 0;
			if(null != newMessagesCursor){
				newMessagesCount = newMessagesCursor.getCount();
			}

            Log.d(TAG, "newMessagesCursor count :" + newMessagesCount);

            if(newMessagesCount > 0){
                CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(lastMessageTimeStamp);
                // Query sent messages since last timestamp and add to the database
                // int missedCount = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(calendar.getTime()));
                while(newMessagesCursor.moveToNext()){
                    String msgId = newMessagesCursor.getString(newMessagesCursor.getColumnIndex(COLUMN_NAME_ID));
                    String sentDate = newMessagesCursor.getString(newMessagesCursor.getColumnIndex(COLUMN_NAME_DATE));
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(sentDate));
                    // Parse and add to message counter database. Skip the new message id which will be added by calling method
                    if(!messageId.equals(msgId)){
                        long index = MessageCounterUtils.getIndexFromDate(cal.getTime());
                        counterDataBase.addMessageSentCounter(index);
                    }
                }
                counterDataBase.close();
            }
			if(null != newMessagesCursor){
				newMessagesCursor.close();
			}
        }
	}

	private void showMessageLimitNotification(CounterDataBaseAdapter counterDataBase) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean sentCountLimitEnabled = preferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);
		boolean notifEnabled = preferences.getBoolean(AppConstants.PREF_KEY_ENABLE_NOTIFICATION, false);
		if (sentCountLimitEnabled && notifEnabled) {
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
