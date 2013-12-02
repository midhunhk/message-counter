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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.adapters.ContactDetailAdapter;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;
import com.ae.apps.messagecounter.vo.ContactMessageVo;

/**
 * Fragment that displays the data as a list
 * 
 * @author Midhun
 * 
 */
public class MessageListFragment extends ListFragment implements MessageDataConsumer {

	private MessageDataReader		mReader;
	private ContactDetailAdapter	adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Create the layout and find the elements
		View layout = inflater.inflate(R.layout.fragment_list, null);

		TextView allMessageCountText = (TextView) layout.findViewById(R.id.allMessageCountText);
		TextView inboxMessageCountText = (TextView) layout.findViewById(R.id.inboxMessageCountText);
		TextView sentMessageCountText = (TextView) layout.findViewById(R.id.sentMessageCountText);

		// Create an empty list for the adapter
		List<ContactMessageVo> data = new ArrayList<ContactMessageVo>();

		// CReate the adapter instance with dummy data
		adapter = new ContactDetailAdapter(getActivity().getBaseContext(), data);
		setListAdapter(adapter);

		// Set the messages count
		int allMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_ALL);
		int sentMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_SENT);
		int inboxMessageCount = mReader.getMessageCount(SMSManager.SMS_URI_INBOX);

		allMessageCountText.setText(getResources().getString(R.string.message_count_all, allMessageCount));
		sentMessageCountText.setText(getResources().getString(R.string.message_count_sent, sentMessageCount));
		inboxMessageCountText.setText(getResources().getString(R.string.message_count_inbox, inboxMessageCount));
		
		if(mReader != null){
			mReader.registerForData(this);
		}
		return layout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			// Register for data
			mReader = (MessageDataReader) activity;
			mReader.registerForData(this);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCheckStatusListener");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDataReady(Object objData) {
		List<ContactMessageVo> temp = (List<ContactMessageVo>) objData;
		if (temp != null && adapter != null) {
			adapter.updateList(temp);
		}
	}
}
