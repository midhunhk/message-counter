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

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ae.apps.messagecounter.R;

/**
 * Settings Activity
 * 
 * TODO add toolbar to this view
 * http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
 * 
 * @author MidhunHK
 *
 */
public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// will use logic from stackoverflow link to show this with toolbr
		addPreferencesFromResource(R.xml.preferences);
		
		/*if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			addPreferencesFromResource(R.xml.preferences);
		} else {
			
		}*/
	}
	
}
