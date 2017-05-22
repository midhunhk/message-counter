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
import com.ae.apps.messagecounter.managers.SentCountDataManager;
import com.ae.apps.messagecounter.receivers.WidgetUpdateReceiver;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.utils.MessagesTableConstants;

import java.util.Calendar;
import java.util.Date;

/**
 * SMSObserver will get invoked when the SMS content provider has any changes
 *
 * @author Midhun
 */
public class SMSObserver extends ContentObserver {

    private static final String TAG = "SMSObserver";

    // Private references that are set from the constructor
    private Uri mObservableUri = null;
    private Context mContext = null;

    /**
     * Initializes the SMSObserver class
     *
     * @param handler      handler
     * @param context      context
     * @param uriToObserve uriToObserve
     */
    public SMSObserver(Handler handler, Context context, Uri uriToObserve) {
        super(handler);
        mContext = context;
        mObservableUri = uriToObserve;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Cursor cursor = mContext.getContentResolver().query(mObservableUri, null, null, null, null);
        if (null != cursor && cursor.moveToNext()) {
            // We need the protocol and the message _id
            String messageId = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_ID));
            String protocol = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_PROTOCOL));
            String sentTime = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_DATE));

            cursor.close();

            // Read lastMessageId from SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String lastMessageId = sharedPreferences.getString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, "");

            // See if this message was processed earlier, sometimes same messageId can come multiple times
            boolean isNewMessage = !lastMessageId.equals(messageId);

            checkForOfflineMessages(messageId, sharedPreferences, lastMessageId, isNewMessage);

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
                Log.d("SendSMSObserver", " An SMS was sent at " + sentTime + " with id " + messageId);
                sharedPreferences.edit()
                        .putString(AppConstants.PREF_KEY_LAST_SENT_MESSAGE_ID, messageId)
                        .apply();

                // Store last message sent timestamp also
                sharedPreferences.edit()
                        .putString(AppConstants.PREF_KEY_LAST_SENT_TIME_STAMP, sentTime)
                        .apply();

                // Send a broadcast to update our widgets
                sendWidgetUpdateBroadcast();
            }
        }
    }

    // Experimental feature - For Unicorn
    // Check for any messages that we might have missed since the last message was logged.
    // If new message ID is different from last saved message ID, check if any messages
    // went un counted. Enable this flag from the preferences menu
    private void checkForOfflineMessages(String messageId, SharedPreferences sharedPreferences, String lastMessageId, boolean isNewMessage) {
        boolean enableOfflineCount = sharedPreferences.getBoolean(AppConstants.PREF_KEY_ENABLE_OFFLINE_COUNT, true);
        if (isNewMessage && enableOfflineCount) {
            SentCountDataManager countDataManager = SentCountDataManager.newInstance();
            Log.d(TAG, "lastMessageId is " + lastMessageId);
            countDataManager.checkForUnLoggedMessages(mContext, messageId, false);
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
            if (currentCount >= userLimit) {
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
                        .setSmallIcon(R.drawable.ic_notification_icon)
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
