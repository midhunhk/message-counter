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

import java.util.Date;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.managers.SentCountDataManager;
import com.ae.apps.messagecounter.services.SMSObserverService;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.SentCountDetailsVo;

/**
 * This fragment hosts the sent messages counter interface
 * 
 * @author Midhun
 * 
 */
public class SentCountFragment extends Fragment {

	private static final String	PROGRESS_VALUE			= "progress";
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
		// Lets find the cycle start date
		Date cycleStartDate = MessageCounterUtils.getCycleStartDate(mPreferences);

		mCycleDurationText.setText(MessageCounterUtils.getDurationDateString(cycleStartDate));

		// Get the sent count details from the database
		SentCountDataManager dataManager = new SentCountDataManager();
		SentCountDetailsVo detailsVo = dataManager.getSentCountData(mContext);

		// set no of messages sent today
		mSentTodayText.setText(detailsVo.getSentToday() + "");

		// set the progressbar
		setProgressInfo(detailsVo.getSentCycle(), detailsVo.getCycleLimit(), mProgressBar, mProgressText, 500);

		// Show the previous cycle details
		int lastCycle = detailsVo.getSentLastCycle();
		Date prevCycleStartDate = MessageCounterUtils.getPrevCycleStartDate(cycleStartDate);
		mPrevCycleSentText.setText(lastCycle + "");
		mPrevCycleDurationText.setText(MessageCounterUtils.getDurationDateString(prevCycleStartDate));

		// set the progressbar for the last cycle
		setProgressInfo(lastCycle, detailsVo.getCycleLimit(), mPrevCountProgressBar, mPrevCycleSentText, 800);

		// Some basic animations
		Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.fade_in);
		fadeInAnimation.setStartOffset(150);
		mSentCounterLayout.startAnimation(fadeInAnimation);
	}

	private void setProgressInfo(int count, int limit, ProgressBar progressBar, TextView progressText, long animDelay) {
		if (limit > 0) {
			progressBar.setMax(limit);
			int progress = count;
			if (count >= limit) {
				// Don't want progress to be greater than limit
				progress = limit;
			}
			setProgressWithAnimation(progressBar, progress, animDelay);
			progressText.setText(count + " / " + limit);
		}
	}

	/**
	 * Sets the progress value to a progressbar, with animation if the OS supports it
	 * 
	 * @param progressBar
	 * @param progress
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void setProgressWithAnimation(ProgressBar progressBar, int progress, long animDelay) {
		// We try to add animation for newer APIs here
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.animation.ObjectAnimator animation = android.animation.ObjectAnimator.ofInt(progressBar,
					PROGRESS_VALUE, 0, progress);
			animation.setDuration(100);
			animation.setStartDelay(animDelay);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.start();
		} else {
			progressBar.setProgress(progress);
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
