/*
 * Copyright 2013 Midhun Harikumar
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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.views.SimpleGraphView;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.ContactMessageVo;
import com.ae.apps.messagecounter.vo.GraphData;

/**
 * A Fragment that displays the data using a chart
 * 
 * @author Midhun
 * 
 */
public class MessageChartFragment extends Fragment implements MessageDataConsumer {

	private MessageDataReader		mReader;
	private Context					mContext;
	private LinearLayout			graphContainer;
	private TextView				titleText;
	private int						inboxMessageCount;
	private List<ContactMessageVo>	contactMessageList;
	private static final int		MAX_ROWS_IN_CHART	= 8;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		
		View layout = inflater.inflate(R.layout.fragment_chart, null);
		mContext = getActivity().getBaseContext();
		// Get the message count in the inbox
		inboxMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_INBOX);
		// Find the Graph Container
		graphContainer = (LinearLayout) layout.findViewById(R.id.graphContainer);
		titleText = (TextView) layout.findViewById(R.id.chartTitle);
		
		mReader.registerForData(this);
		return layout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mReader = (MessageDataReader) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCheckStatusListener");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDataReady(Object data) {
		// We can hide the progressbar at this point
		// Try to convert the data into a list
		contactMessageList = (List<ContactMessageVo>) data;
		if (contactMessageList != null && inboxMessageCount > 0) {
			titleText.setText(getResources().getString(R.string.str_chart_title));
			GraphData graphData = MessageCounterUtils.getMessageCountDegrees(contactMessageList, inboxMessageCount,
					MAX_ROWS_IN_CHART, false);
			//Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.fade_in);
			// Create a new SimpleGraphView and add it to the graphContainer
			View graphView = new SimpleGraphView(mContext, graphData.getValueInDegrees(), graphData.getLabels(),
					AppConstants.CHART_COLORFUL);
			//graphView.startAnimation(fadeInAnimation);
			graphContainer.addView(graphView);
		}
	}
}