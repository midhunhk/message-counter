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
package com.ae.apps.messagecounter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.fragments.MessageChartFragment;
import com.ae.apps.messagecounter.fragments.MessageListFragment;
import com.ae.apps.messagecounter.fragments.SentCountFragment;

/**
 * The Pager Adapter for the pager menu used in this app
 * 
 * @author midhun
 * 
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	private final Context	context;
	private final Fragment	mListFragment;
	private final Fragment	mChartFragment;
	private final Fragment	mSentCountFragment;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		mListFragment = new MessageListFragment();
		mChartFragment = new MessageChartFragment();
		mSentCountFragment = new SentCountFragment();
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = null;
		switch (i) {
		case 0:
			fragment = mSentCountFragment;
			break;
		case 1:
			fragment = mListFragment;
			break;
		case 2:
			fragment = mChartFragment;
			break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return context.getString(R.string.title_section1).toUpperCase();
		case 1:
			return context.getString(R.string.title_section3).toUpperCase();
		}
		return null;
	}

}
