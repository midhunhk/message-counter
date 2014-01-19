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

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.db.MessageCounterDataBase;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class SentCountFragment extends Fragment {

	private Context	mContext	= null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_sent_count, null);
		mContext = getActivity().getBaseContext();

		MessageCounterDataBase counterDataBase = new MessageCounterDataBase(mContext);
		counterDataBase.addMessageSentCounter(MessageCounterUtils.getIndexFromDate(Calendar.getInstance().getTime()));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		int count = counterDataBase.getTotalSentCountSinceDate(MessageCounterUtils.getIndexFromDate(calendar.getTime()));

		ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.countProgressBar);
		progressBar.setMax(20);
		progressBar.setProgress(count);
		return layout;
	}

}
