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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	private TextView			mCycleDurationText		= null;
	private TextView			mPrevCycleDurationText	= null;
	private TextView			mPrevCycleSentText		= null;
	private ProgressBar			mPrevCountProgressBar	= null;
	private boolean				mCachedPreferenceValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_sent_count, null);
		mContext = getActivity().getBaseContext();

		// get the preferences for the app
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

		// Find ui elements
		mProgressText = (TextView) layout.findViewById(R.id.countProgressText);
		mSentTodayText = (TextView) layout.findViewById(R.id.countSentTodayText);
		mProgressBar = (ProgressBar) layout.findViewById(R.id.countProgressBar);
		mSentCounterLayout = layout.findViewById(R.id.sentCounterLayout);
		mStartCountingLayout = layout.findViewById(R.id.startCountingLayout);
		mCycleDurationText = (TextView) layout.findViewById(R.id.cycleDurationText);
		mPrevCycleSentText = (TextView) layout.findViewById(R.id.prevCycleSentCountText);
		mPrevCycleDurationText = (TextView) layout.findViewById(R.id.prevCycleDurationText);
		mPrevCountProgressBar = (ProgressBar) layout.findViewById(R.id.prevCountProgressBar);

		// See which layout to be shown to the user
		updateLayout();

		// Cache the enabled preference value
		cacheEnabledPref();

		return layout;
	}

	private void updateLayout() {
		boolean enabled = getCountMessagesEnabledPref();
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
		// Create a db adapter and start reading from it
		CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);

		// Lets find the cycle start date
		Date cycleStartDate = MessageCounterUtils.getCycleStartDate(mPreferences);

		mCycleDurationText.setText(MessageCounterUtils.getDurationDateString(cycleStartDate));
		Date today = Calendar.getInstance().getTime();

		// Find the no of messages sent today
		int sentTodayCount = counterDataBase.getCountValueForDay(MessageCounterUtils.getIndexFromDate(today));
		if (sentTodayCount == -1) {
			sentTodayCount = 0;
		}
		mSentTodayText.setText(sentTodayCount + "");

		// and now the sent messages count from the start date
		int count = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(cycleStartDate));

		// Get the limit the user has specified
		int limit = MessageCounterUtils.getMessageLimitValue(mPreferences);

		// set the correct values for the progressbar
		setProgressInfo(count, limit, mProgressBar, mProgressText);

		// Show the previous cycle details
		Date prevCycleStartDate = MessageCounterUtils.getPrevCycleStartDate(cycleStartDate);
		int lastCycleCount = MessageCounterUtils.getCycleSentCount(counterDataBase, prevCycleStartDate);
		mPrevCycleSentText.setText(lastCycleCount + "");
		mPrevCycleDurationText.setText(MessageCounterUtils.getDurationDateString(prevCycleStartDate));

		setProgressInfo(lastCycleCount, limit, mPrevCountProgressBar, mPrevCycleSentText);

		// Close the db connection
		counterDataBase.close();

		// Some basic animations
		Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.fade_in);
		fadeInAnimation.setStartOffset(150);
		mSentCounterLayout.startAnimation(fadeInAnimation);
	}

	public void setProgressInfo(int count, int limit, ProgressBar progressBar, TextView progressText) {
		if (limit > 0) {
			progressBar.setMax(limit);
			if (count >= limit) {
				// Don't want progress to be greater than limit
				progressBar.setProgress(limit);
			} else {
				progressBar.setProgress(count);
			}
			progressText.setText(count + " / " + limit);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateLayout();

		boolean enabled = getCountMessagesEnabledPref();
		// If ecount messages is enabled, we should prolly start the service
		if (enabled != mCachedPreferenceValue && enabled) {
			mContext.startService(new Intent(mContext, SMSObserverService.class));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		cacheEnabledPref();
	}

	private void cacheEnabledPref() {
		// we cache the preference value so that we can check if this was changed in settings
		mCachedPreferenceValue = getCountMessagesEnabledPref();
	}

	private boolean getCountMessagesEnabledPref() {
		return mPreferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);
	}

}
