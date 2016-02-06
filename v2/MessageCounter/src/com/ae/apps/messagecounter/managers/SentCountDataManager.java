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
import android.preference.PreferenceManager;

import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.SentCountDetailsVo;

/**
 * Manager for retrieving all the sent count data ready for the UI from database
 * 
 * @author Midhun
 * 
 */
public class SentCountDataManager {

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

}
