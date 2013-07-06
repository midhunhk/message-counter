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

package com.ae.apps.messagecounter.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;
import com.ae.apps.messagecounter.fragments.MessageChartFragment;
import com.ae.apps.messagecounter.fragments.MessageListFragment;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.ContactMessageVo;

/**
 * Main Activity for the project
 * 
 * @author Midhun
 * 
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener, MessageDataReader {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
	 * If this becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter				mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager							mViewPager;

	private boolean						isDataReady;
	private Handler						handler;
	private ContactManager				mContactManager;
	private SMSManager					mSmsManager;
	private ProgressDialog				loadingDialog;
	private List<MessageDataConsumer>	consumers = new ArrayList<MessageDataConsumer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContactManager = new ContactManager(getContentResolver());

		mSmsManager = new SMSManager(getBaseContext());

		getActionBar().setDisplayHomeAsUpEnabled(false);
		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding tab.
		// We can also use ActionBar.Tab#select() to do this if we have a reference to the
		// Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}

		// Show a progress dialog
		loadingDialog = ProgressDialog.show(this, getResources().getString(R.string.title_loading), getResources()
				.getString(R.string.message_loading));

		// Create the handler in the main thread
		handler = new Handler();

		// initialize the data
		isDataReady = false;

		// Do the read and parse operations in a new thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// Get the contact ids for which there are sms es
				ContactVo contactVo = null;
				ContactMessageVo contactMessageVo = null;
				final List<ContactMessageVo> contactMessageList = new ArrayList<ContactMessageVo>();

				// Get a collection of message senders and message count and sort it
				Map<String, Integer> messageSendersMap = mSmsManager.getUniqueSenders();
				Map<String, Integer> sortedValuesMap = MessageCounterUtils.sortThisMap(messageSendersMap);

				// Collate the ContactVo, photo bitmap and message count and create the data for the list
				String contactId = null;
				Integer messageCount = null;
				Set<String> rawContactIdskeySet = sortedValuesMap.keySet();
				for (String rawContactId : rawContactIdskeySet) {
					messageCount = messageSendersMap.get(rawContactId);
					// Get the contactId corresponding to this rawContactId
					contactId = String.valueOf(mContactManager.getContactIdFromRawContactId(rawContactId));
					contactVo = mContactManager.getContactInfo(contactId);

					// Create a new ContactMessageVo Map object and add to the list
					contactMessageVo = new ContactMessageVo();
					contactMessageVo.setContactVo(contactVo);
					if (messageCount != null) {
						contactMessageVo.setMessageCount(messageCount);
					}
					contactMessageVo.setPhoto(mContactManager.getContactPhoto(contactId));

					contactMessageList.add(contactMessageVo);
				}

				// We finally have the data with us
				final List<ContactMessageVo> data = contactMessageList;
				isDataReady = true;

				// Dismiss the loading dialog
				loadingDialog.dismiss();

				handler.post(new Runnable() {

					@Override
					public void run() {
						// Inform the consumers that the data is ready
						for (MessageDataConsumer consumer : consumers) {
							consumer.onDataReady(data);
						}
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_license:
			DialogUtils.showWithMessageAndOkButton(this, R.string.menu_license, R.string.str_license_text,
					android.R.string.ok);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DummySectionFragment();
			if (i == 0) {
				fragment = new MessageListFragment();
			} else if (i == 1) {
				fragment = new MessageChartFragment();
			} else {
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
				fragment.setArguments(args);
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
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
				// case 2: return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}

		public static final String	ARG_SECTION_NUMBER	= "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			Bundle args = getArguments();
			textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
			return textView;
		}
	}

	@Override
	public void registerForData(MessageDataConsumer consumer) {
		if (consumers == null) {
			consumers = new ArrayList<MessageDataConsumer>();
		}
		consumers.add(consumer);
		// if data is ready, invoke the data ready method
		if (isDataReady) {
			consumer.onDataReady(null);
		}
	}

	@Override
	public SMSManager getSmsManagerInstance() {
		return mSmsManager;
	}

	@Override
	public ContactManager getContactManagerInstance() {
		return mContactManager;
	}
}