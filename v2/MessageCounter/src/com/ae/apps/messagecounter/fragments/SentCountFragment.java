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

package com.ae.apps.messagecounter.fragments;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.services.SMSObserverService;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

/**
 * This fragment hosts the sent messages counter interface
 * 
 * @author Midhun
 * 
 */
public class SentCountFragment extends Fragment {

	private Context				mContext				= null;
	private View				mSentCounterLayout		= null;
	private View				mStartCountingLayout	= null;
	private ProgressBar			mProgressBar			= null;
	private SharedPreferences	mPreferences			= null;
	private TextView			mProgressText			= null;
	private TextView			mSentTodayText			= null;
	private TextView			mCycleStartText			= null;
	private TextView			mCycleEndText			= null;
	private boolean				mCachedPreferenceValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_sent_count, null);
		mContext = getActivity().getBaseContext();
		// get the
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

		// Find ui elements
		mProgressText = (TextView) layout.findViewById(R.id.countProgressText);
		mSentTodayText = (TextView) layout.findViewById(R.id.countSentTodayText);
		mProgressBar = (ProgressBar) layout.findViewById(R.id.countProgressBar);
		mSentCounterLayout = layout.findViewById(R.id.sentCounterLayout);
		mStartCountingLayout = layout.findViewById(R.id.startCountingLayout);
		mCycleStartText = (TextView) layout.findViewById(R.id.currentCycleStartDateText);
		mCycleEndText = (TextView) layout.findViewById(R.id.currentCycleEndDateText);

		// See which layout to be shown to the user
		toggleLayoutContent();

		return layout;
	}

	private void toggleLayoutContent() {
		boolean enabled = mPreferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);
		if (enabled == false) {
			mSentCounterLayout.setVisibility(View.GONE);
			mStartCountingLayout.setVisibility(View.VISIBLE);
		} else {
			showSentCountDetails();
			mSentCounterLayout.setVisibility(View.VISIBLE);
			mStartCountingLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * This method sets up the data that needs to be displyed if we have to show the content
	 */
	private void showSentCountDetails() {
		// Create the db adapter and start reading from it
		CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);

		// Lets find the cycle start date
		int cycleStart = Integer.valueOf(mPreferences.getString(AppConstants.PREF_KEY_CYCLE_START_DATE,
				AppConstants.DEFAULT_CYCLE_START_DATE));

		Date cycleStartDate = getCurrentCycleStartDate(cycleStart);
		mCycleStartText.setText(MessageCounterUtils.getDisplayDateString(cycleStartDate));
		mCycleEndText.setText(MessageCounterUtils.getDisplayDateString(getCurrentCycleEndDate(cycleStartDate)));
		Date today = Calendar.getInstance().getTime();

		// Find the no of messages sent today
		int sentTodayCount = counterDataBase.getCountValueForDay(MessageCounterUtils.getIndexFromDate(today));
		mSentTodayText.setText(sentTodayCount + "");

		// and now the sent messages count from the start date
		int count = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(cycleStartDate));

		// Get the limit the user has specified, handle empty values as well
		String rawVal = mPreferences.getString(AppConstants.PREF_KEY_MESSAGE_LIMIT_VALUE, "-1");
		int limit = AppConstants.DEFAULT_MESSAGE_LIMIT;
		try {
			limit = Integer.valueOf(rawVal);
		} catch (NumberFormatException e) {
			// we default to 100 if user left the setting empty
			limit = AppConstants.DEFAULT_MESSAGE_LIMIT;
		}

		if (limit > 0) {
			mProgressBar.setMax(limit);
			if (count >= limit) {
				// Don't want progress to be greater than limit
				mProgressBar.setProgress(limit);
			} else {
				mProgressBar.setProgress(count);
			}
			mProgressText.setText(count + " / " + limit);
		}
		// Close the db connection
		counterDataBase.close();
	}

	private Date getCurrentCycleStartDate(int cycleStart) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DATE) < cycleStart) {
			calendar.set(Calendar.MONTH, -1);
		}
		calendar.set(Calendar.DATE, cycleStart);
		return calendar.getTime();
	}

	/**
	 * By default, cycle duration will be a month
	 * 
	 * @param startDate
	 * @return
	 */
	private Date getCurrentCycleEndDate(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
		return calendar.getTime();
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean enabled = getCountMessagesEnabledPref();

		// If the pref is changed, then we should probably toggle the content displayed
		if (enabled != mCachedPreferenceValue) {
			toggleLayoutContent();
		}

		// If enabled pref is changed and enabled, we might need to start the service
		if ((enabled != mCachedPreferenceValue) && enabled) {
			mContext.startService(new Intent(mContext, SMSObserverService.class));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// we cache the preference value so that we can check if this was changed in settings
		mCachedPreferenceValue = getCountMessagesEnabledPref();
	}

	private boolean getCountMessagesEnabledPref() {
		return mPreferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);
	}

}
