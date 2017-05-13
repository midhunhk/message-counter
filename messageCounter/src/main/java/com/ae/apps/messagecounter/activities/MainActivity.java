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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ae.apps.common.activities.ToolBarBaseActivity;
import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.mock.MockContactDataUtils;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.adapters.NavDrawerListAdapter;
import com.ae.apps.messagecounter.adapters.SectionsPagerAdapter;
import com.ae.apps.messagecounter.data.MessageDataConsumer;
import com.ae.apps.messagecounter.data.MessageDataReader;
import com.ae.apps.messagecounter.fragments.SentCountFragment;
import com.ae.apps.messagecounter.utils.AppConstants;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;
import com.ae.apps.messagecounter.vo.NavDrawerItem;

/**
 * Main Activity and one entry point to this application
 * 
 * @author Midhun
 * 
 */
public class MainActivity extends ToolBarBaseActivity implements MessageDataReader, OnMenuItemClickListener,
		OnItemClickListener, View.OnClickListener {

	private boolean						isDataReady;
	private Handler						mHandler;
	private DrawerLayout				mDrawerLayout;
	private ListView					mDrawerList;
	private List<ContactMessageVo>		mContactMessageList;
	private SectionsPagerAdapter		mSectionsAdapter;
	private final Map<String, Integer>	messageCountsCache	= new HashMap<>();
	private List<MessageDataConsumer>	mConsumers			= new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSectionsAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
		
		if (null == savedInstanceState) {
			// Message Counter is the default fragment
			getSupportFragmentManager().beginTransaction().add(R.id.container, new SentCountFragment()).commit();
			setToolbarTitle(mSectionsAdapter.getPageTitle(0));
		}

		final SMSManager smsManager = new SMSManager(getBaseContext());
		final ContactManager.Config config = new ContactManager.Config();
		config.contentResolver = getContentResolver();
		config.addContactsWithPhoneNumbers = false;
		final ContactManager contactManager = new ContactManager(config);

		// Cache the message counts
		messageCountsCache.put(SMSManager.SMS_URI_ALL, smsManager.getMessagesCount(SMSManager.SMS_URI_ALL));
		messageCountsCache.put(SMSManager.SMS_URI_SENT, smsManager.getMessagesCount(SMSManager.SMS_URI_SENT));
		messageCountsCache.put(SMSManager.SMS_URI_INBOX, smsManager.getMessagesCount(SMSManager.SMS_URI_INBOX));
		messageCountsCache.put(SMSManager.SMS_URI_DRAFTS, smsManager.getMessagesCount(SMSManager.SMS_URI_DRAFTS));

		// Navigation Drawer
		List<NavDrawerItem> navItems = new ArrayList<>();
		
		// Create the list for the main fragments to be shown in the drawer
		NavDrawerListAdapter drawerListAdapter = new NavDrawerListAdapter(this, navItems);
		navItems.add(new NavDrawerItem(R.drawable.nav_icon_email, mSectionsAdapter.getPageTitle(0)));
		navItems.add(new NavDrawerItem(R.drawable.nav_icon_list_bulleted, mSectionsAdapter.getPageTitle(1)));
		navItems.add(new NavDrawerItem(R.drawable.nav_icon_chart_pie, mSectionsAdapter.getPageTitle(2)));
		
		mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
		mDrawerList.setAdapter(drawerListAdapter);
		mDrawerList.setOnItemClickListener(this);
		
		displayHomeAsUp();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				getToolBar(),
				R.string.app_name,
				R.string.app_name);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		// End changes for Navigation Drawer
		
		// Handle clicks for Donate link from navigation drawer
		findViewById(R.id.navDonate).setOnClickListener(this);
		findViewById(R.id.navSettings).setOnClickListener(this);
		// findViewById(R.id.navAbout).setOnClickListener(this);
		// findViewById(R.id.navShare).setOnClickListener(this);

		// Create the handler in the main thread
		mHandler = new Handler();

		// the data is not yet ready
		isDataReady = false;

		// Do the read and parse operations in a new thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<ContactMessageVo> data;
				boolean isMockedRun = Boolean.parseBoolean("false");
				if (isMockedRun) {
					// We are doing a mock run, most probably to take some screenshots
					data = MockContactDataUtils.getMockContactMessageList(getResources());
				} else {
					// Get the mapping of address and message count
					Map<String, Integer> messageSendersMap = smsManager.getUniqueSenders();
					// Convert to mapping of contact and message count
					messageSendersMap = MessageCounterUtils.convertAddressToContact(contactManager, messageSendersMap);
					// Sorting the map based on message count
					Map<String, Integer> sortedValuesMap = MessageCounterUtils.sortThisMap(messageSendersMap);
					// Convert this data to a list of ContactMessageVos
					data = MessageCounterUtils
							.getContactMessageList(contactManager, sortedValuesMap, messageSendersMap);
				}
				isDataReady = true;
				mContactMessageList = data;

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// Inform the consumers that the data is ready
						for (MessageDataConsumer consumer : mConsumers) {
							consumer.onDataReady(data);
						}
					}
				});
			}
		}).start();

		// Inflate and handle menu clicks
		getToolBar().inflateMenu(R.menu.activity_main);
		
		showNavDrawerIntro();
	}

	@SuppressLint("RtlHardcoded")
	private void showNavDrawerIntro() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
		boolean helloNavDrawer = sharedPreferences.getBoolean(AppConstants.PREF_KEY_NAV_DRAWER_INTRO_GIVEN, false);
		
		// Check and introduce the Navigation Drawer on first use to the user
		if(null != mDrawerLayout && !helloNavDrawer){
			mDrawerLayout.openDrawer(Gravity.LEFT);
			sharedPreferences
				.edit()
				.putBoolean(AppConstants.PREF_KEY_NAV_DRAWER_INTRO_GIVEN, true)
				.apply();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return handleMenuItemClick(item);
	}

	@SuppressLint({ "InlinedApi", "RtlHardcoded" })
	private boolean handleMenuItemClick(MenuItem item) {

		setDrawerState(item);

		switch (item.getItemId()) {
		case R.id.menu_share_app:
			// Share this app
			startActivity(getShareIntent());
			return true;
		/*case R.id.menu_settings:
			// Display the preference screen
			startActivity(new Intent(this, SettingsActivity.class));
			return true;*/
		case R.id.menu_about:
			// Show the about screen
			startActivity(new Intent(this, AboutActivity.class));
			return false;
		/*case R.id.menu_donate:
			startActivity(new Intent(this, DonationsActivity.class));
			return false;*/
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("RtlHardcoded")
	private void setDrawerState(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				mDrawerLayout.closeDrawers();
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawers();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void registerForData(MessageDataConsumer consumer) {
		if (mConsumers == null) {
			mConsumers = new ArrayList<>();
		}
		mConsumers.add(consumer);

		// if data is ready, perform the call back as per the contract
		if (isDataReady) {
			consumer.onDataReady(mContactMessageList);
		}
	}

	@Override
	public int getMessageCount(String type) {
		if (messageCountsCache != null && messageCountsCache.containsKey(type)){
				return messageCountsCache.get(type);
		}
		return 0;
	}

	private Intent getShareIntent() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.play_store_url));
		shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.menu_share));
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		return shareIntent;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return handleMenuItemClick(item);
	}

	@Override
	protected int getToolbarResourceId() {
		return R.id.toolbar;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_main;
	}

	/**
	 * Click handler for Navigation Drawer list item
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showFragmentContent(position);
	}

	private void showFragmentContent(int position) {
		Fragment fragment = mSectionsAdapter.getItem(position);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
		
		// highlight the selected item and close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawers();

		// Display the section header in the title
		setToolbarTitle(mSectionsAdapter.getPageTitle(position));
	}

	@Override
	public void onClick(View v) {
		mDrawerLayout.closeDrawers();
		final Context context = getBaseContext();
		
		switch(v.getId()){
		case R.id.navDonate:
			startActivity(new Intent(context, DonationsActivity.class));
			break;
			
		/*case R.id.navShare:
			// Share this app
			startActivity(getShareIntent());
			break;*/
			
		case R.id.navSettings:
			// Display the preference screen
			startActivity(new Intent(this, SettingsActivity.class));
			break;
			
		/*case R.id.navAbout:
			// Show the about screen
			startActivity(new Intent(this, AboutActivity.class));
			break;*/
		}
	}

}