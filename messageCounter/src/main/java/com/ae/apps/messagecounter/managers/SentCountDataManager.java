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

package com.ae.apps.messagecounter.managers;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.utils.MessagesTableConstants;
import com.ae.apps.messagecounter.vo.SentCountDetailsVo;

/**
 * Manager for retrieving all the sent count data ready for the UI from database
 * 
 * @author Midhun
 * 
 */
public class SentCountDataManager {

	private static final String TAG = "SentCountDataManager";

	public SentCountDetailsVo getSentCountData(Context context) {
		// Get the user preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		// Get the limit the user has specified
		int limit = MessageCounterUtils.getMessageLimitValue(preferences);

		// Cycle start date is also available in the preferences
		Date cycleStartDate = MessageCounterUtils.getCycleStartDate(preferences);
		Date today = Calendar.getInstance().getTime();

		// Initialize and open the database
		CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(context);

		// Find the no of messages sent today
		int sentTodayCount = counterDataBase.getCountValueForDay(MessageCounterUtils.getIndexFromDate(today));

		// and now the sent messages count from the start date
		int sentCycleCount = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(cycleStartDate));

		Date prevCycleStartDate = MessageCounterUtils.getPrevCycleStartDate(cycleStartDate);
		int lastCycleCount = MessageCounterUtils.getCycleSentCount(counterDataBase, prevCycleStartDate);
		
		// Messages sent this week
		Date weekStartDate = MessageCounterUtils.getWeekStartDate();
		int sentWeekCount = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(weekStartDate));

		// Close the db connection
		counterDataBase.close();

		if (sentTodayCount == -1) {
			sentTodayCount = 0;
		}

		// Form the details vo
		SentCountDetailsVo detailsVo = new SentCountDetailsVo();
		detailsVo.setSentToday(sentTodayCount);
		detailsVo.setSentCycle(sentCycleCount);
		detailsVo.setSentLastCycle(lastCycleCount);
		detailsVo.setCycleLimit(limit);
		detailsVo.setSentInWeek(sentWeekCount);

		return detailsVo;
	}

	/**
	 * Check for sent messages that were not tracked by the background service
	 *
     * @param context the context
	 * @param messageId     the new message's id
     * @param indexAllMessages whether to index all messages in the ms table
	 * @return number of messages added
	 */
	public int checkForUnLoggedMessages(Context context, String messageId, boolean indexAllMessages) {
		Log.d(TAG, "Enter checkForUnLoggedMessages : messageId " + messageId);

        String defaultTimeStamp = "";
        if(indexAllMessages){
            defaultTimeStamp = "0";
        }

		// Read lastMessageTimeStamp from SharedPreferences as the lastMessage may be removed by the user
		String lastMessageTimeStamp = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(AppConstants.PREF_KEY_LAST_SENT_TIME_STAMP, defaultTimeStamp);
		Log.d(TAG, "lastMessageTimeStamp :" + lastMessageTimeStamp);

		int newMessagesAdded = 0;
		// When we get valid lastMessageTimeStamp, we can check how many messages came were sent afterwards
		if (!TextUtils.isEmpty(lastMessageTimeStamp)) {
			Cursor newMessagesCursor = context.getContentResolver().query(
					Uri.parse(SMSManager.SMS_URI_ALL),
                    MessagesTableConstants.SMS_TABLE_PROJECTION,
					"person is null and " + MessagesTableConstants.COLUMN_NAME_DATE + "> ? ",
					new String[]{String.valueOf(lastMessageTimeStamp)}, null);

			int newMessagesCount = (null != newMessagesCursor) ? newMessagesCursor.getCount() : 0;
			Log.d(TAG, "newMessagesCursor count :" + newMessagesCount);

			try {
				if (newMessagesCount > 0 && newMessagesCursor.moveToFirst()) {
					Log.d(TAG, "Open MessageCounterDatabase");
					// Connect to the App's database
					CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(context);

					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(Long.valueOf(lastMessageTimeStamp));

					Calendar cal = Calendar.getInstance();
					Log.d(TAG, "Start loop over result, pos " + newMessagesCursor.getPosition());
					do {
						Log.d(TAG, "newMessagesCursor.getPosition() " + newMessagesCursor.getPosition());
						String msgId = newMessagesCursor.getString(newMessagesCursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_ID));
						String sentDate = newMessagesCursor.getString(newMessagesCursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_DATE));
						String protocol = newMessagesCursor.getString(newMessagesCursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_PROTOCOL));

						Log.d(TAG, "message (" + msgId + ") was sent on " + sentDate);
						Log.d(TAG, "protocol is" + protocol);

						cal.setTimeInMillis(Long.parseLong(sentDate));
						// Parse and add to message counter database.
						// Skip the new message id which will be added by calling method
						if (!messageId.equals(msgId) && null == protocol) {
							// Count this message against the date it was sent
							long dateIndex = MessageCounterUtils.getIndexFromDate(cal.getTime());
							Log.d(TAG, "Adding message (" + msgId + ") to CounterDataBase");
							counterDataBase.addMessageSentCounter(dateIndex);
							newMessagesAdded++;
						}
					} while (newMessagesCursor.moveToNext());

					counterDataBase.close();
				}
				if (null != newMessagesCursor) {
					newMessagesCursor.close();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
		Log.d(TAG, "Exit checkForUnLoggedMessages, added " + newMessagesAdded + " messages");
		return newMessagesAdded;
	}

}
