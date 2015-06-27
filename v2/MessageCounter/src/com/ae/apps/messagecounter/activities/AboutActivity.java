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

package com.ae.apps.messagecounter.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ae.apps.common.activities.ToolBarBaseActivity;
import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.messagecounter.R;

/**
 * The about activity
 * 
 * @author Midhun
 *
 */
public class AboutActivity extends ToolBarBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show the back arrow in toolbar to go back
		displayHomeAsUp();

		setToolbarTitle(getString(R.string.menu_about));

		View sourceCode = findViewById(R.id.viewSourceCode);
		sourceCode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Launch URL to GitHub page

			}
		});

		final Context context = this;
		View license = findViewById(R.id.viewLicense);
		license.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogUtils.showMaterialInfoDialog(context, R.string.menu_license, R.string.str_license_text,
						android.R.string.ok);
			}
		});
	}

	@Override
	protected int getToolbarResourceId() {
		return R.id.toolbar;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_about;
	}
}
