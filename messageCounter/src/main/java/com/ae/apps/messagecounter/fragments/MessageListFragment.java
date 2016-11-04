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

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.adapters.ContactDetailAdapter;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;

/**
 * Fragment that displays the message count details as a list
 * 
 * @author Midhun
 * 
 */
public class MessageListFragment extends ListFragment implements MessageDataConsumer {

	private MessageDataReader		mReader;
	private ContactDetailAdapter	adapter;
	private TextView				pageTitleText;
	private boolean					loadAnimationDone;
	private ProgressBar				mProgressBar;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);

		try {
			mReader = (MessageDataReader) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + " must implement registerForData");
		}
		
		// Create the layout and find the elements
		View layout = inflater.inflate(R.layout.fragment_list, null);
		mProgressBar = (ProgressBar) layout.findViewById(R.id.loadingProgressBar);

		TextView messageCountGlanceText = (TextView) layout.findViewById(R.id.messageCountAtGlanceText);

		pageTitleText = (TextView) layout.findViewById(R.id.pageTitleText);
		if (loadAnimationDone == false) {
			pageTitleText.setVisibility(View.INVISIBLE);
		}

		// Create an empty list for the adapter
		List<ContactMessageVo> data = new ArrayList<ContactMessageVo>();

		// CReate the adapter instance with dummy data
		adapter = new ContactDetailAdapter(getActivity().getBaseContext(), data);
		setListAdapter(adapter);

		// Set the messages count
		int allMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_ALL);
		int sentMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_SENT);
		int inboxMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_INBOX);
		int draftMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_DRAFTS);

		// Build the summary of message counts string
		String summary = getMessageCountsSummary(allMessageCount, sentMessageCount, inboxMessageCount, draftMessageCount);
		messageCountGlanceText.setText(summary);

		// Wait till the data is loaded and get a callback once its ready
		mReader.registerForData(this);

		return layout;
	}

	private String getMessageCountsSummary(int allMessageCount, int sentMessageCount, int inboxMessageCount,
			int draftMessageCount) {
		StringBuilder builder = new StringBuilder();
		builder.append(getResources().getString(R.string.message_count_sent, sentMessageCount)).append(
				" + " + getResources().getString(R.string.message_count_inbox, inboxMessageCount));
		if (draftMessageCount > 0) {
			builder.append(" + " + getResources().getString(R.string.message_count_draft, draftMessageCount));
		}
		builder.append(" = " + getResources().getString(R.string.message_count_all, allMessageCount));

		String str = builder.toString();
		return str;
	}

	/**
	 * The callback method that will be invoked when the data is ready
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onDataReady(Object objData) {
		List<ContactMessageVo> temp = (List<ContactMessageVo>) objData;
		if (temp != null && adapter != null) {
			adapter.updateList(temp);

			if (loadAnimationDone == false) {
				loadAnimationDone = true;

				// hide the progressbar
				mProgressBar.setVisibility(View.INVISIBLE);

				// Make some hidden views visible with some animation
				if (null != getActivity() && null != getActivity().getBaseContext()) {
					Animation slideInAnimation = AnimationUtils.loadAnimation(getActivity().getBaseContext(),
							R.animator.slide_in_top);
					// add a small delay before starting the animation
					slideInAnimation.setStartOffset(500);
				}
				pageTitleText.setVisibility(View.VISIBLE);
			}
		}
	}
}
