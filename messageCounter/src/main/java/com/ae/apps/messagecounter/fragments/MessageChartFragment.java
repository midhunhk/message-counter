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

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.views.SimpleGraphView;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.GraphData;

/**
 * A Fragment that displays the data using a chart
 * 
 * @author Midhun
 * 
 */
public class MessageChartFragment extends Fragment implements MessageDataConsumer {

	private static final String	TAG	= "MessageChartFragment";
	private MessageDataReader		mReader;
	private Context					mContext;
	private LinearLayout			mGraphContainer;
	private TextView				mTitleText;
	private TextView				mOtherSendersText;
	private int						mInboxMessageCount;
	private List<ContactMessageVo>	mContactMessageList;
	private boolean					mCachedSettingsValue;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);

		try {
			mReader = (MessageDataReader) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + " must implement registerForData");
		}
		
		View layout = inflater.inflate(R.layout.fragment_chart, null);
		mContext = getActivity().getBaseContext();
		
		// Get the message count in the inbox
		mInboxMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_INBOX);
		
		// Find the UI elements
		mTitleText = (TextView) layout.findViewById(R.id.chartTitle);
		mGraphContainer = (LinearLayout) layout.findViewById(R.id.graph_container);
		mOtherSendersText = (TextView) layout.findViewById(R.id.otherSendersText);

		try{
			mTitleText.setText(getResources().getString(R.string.str_chart_title).toUpperCase());
		} catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		mReader.registerForData(this);
		return layout;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDataReady(Object data) {
		// We can hide the progressbar at this point
		// Try to convert the data into a list
		mContactMessageList = (List<ContactMessageVo>) data;
		if (mContactMessageList != null && mInboxMessageCount > 0) {
			updateMessagesChart();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// Save the current preference value
		mCachedSettingsValue = getIncludeNonContactMessagesPref();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Update the chart if the cached value is different from the current preference value,
		// i18n values are taken from parent activity's context,
		// IllegalStateException will be thrown if this Fragment is not currently added to it
		if (mCachedSettingsValue != getIncludeNonContactMessagesPref() && mContactMessageList != null && isAdded()) {
			updateMessagesChart();
		}
	}

	private void updateMessagesChart() {
		try {
			// Reports of IllegalStateExceptions are received

			// Get the preference value for including message counts from non contacts
			boolean includeNonContactMessages = getIncludeNonContactMessagesPref();

			// Do we need to show the other senders text?
			if (includeNonContactMessages) {
				mOtherSendersText.setVisibility(View.GONE);
			} else {
				mOtherSendersText.setVisibility(View.VISIBLE);
			}

			GraphData graphData = MessageCounterUtils.getMessageCountDegrees(mContactMessageList, mInboxMessageCount,
					AppConstants.MAX_ROWS_IN_CHART, includeNonContactMessages);
			View graphView = new SimpleGraphView(mContext, graphData.getValueInDegrees(), graphData.getLabels(),
					AppConstants.CHART_COLORFUL2);

			// If we are updating, remove the previous graphView
			if (mGraphContainer.getChildCount() > 0) {
				mGraphContainer.removeAllViews();
			}

			// Create a new SimpleGraphView and add it to the graphContainer
			Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.fade_in);
			fadeInAnimation.setStartOffset(150);
			graphView.startAnimation(fadeInAnimation);
			mGraphContainer.addView(graphView);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Returns the current value for hide past messages
	 * 
	 * @return
	 */
	private boolean getIncludeNonContactMessagesPref() {
		boolean includeNonContactMessagePref = false;
		try {
			// reports of an exception being thrown here in some cases
			includeNonContactMessagePref = PreferenceManager
					.getDefaultSharedPreferences(getActivity().getBaseContext()).getBoolean(
							AppConstants.PREF_KEY_HIDE_NON_CONTACT_MESSAGES, false);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return includeNonContactMessagePref;
	}

}
