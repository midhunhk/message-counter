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

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.ae.apps.messagecounter.R;

/**
 * Settings Activity
 * 
 * @author MidhunHK
 *
 */
public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set a content view that has a toolbar and list with android:id 
		setContentView(R.layout.activity_settings);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		// add the preference items
		addPreferencesFromResource(R.xml.preferences);
		
		// make the toolbar behave like an ActionBar
		toolbar.setClickable(true);
		toolbar.setNavigationIcon(getResIdFromAttribute(this, R.attr.homeAsUpIndicator));
		toolbar.setTitle(R.string.menu_settings);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	private static int getResIdFromAttribute(final Activity activity, final int attr) {
		if (attr == 0) {
			return 0;
		}
		final TypedValue typedvalueattr = new TypedValue();
		activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
		return typedvalueattr.resourceId;
	}
}
