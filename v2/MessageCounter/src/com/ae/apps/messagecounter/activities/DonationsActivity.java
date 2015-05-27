/*
 * Copyright 2015 Midhun Harikumar
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ae.apps.common.activities.DonationsBaseActivity;
import com.ae.apps.common.utils.inapp.IabResult;
import com.ae.apps.common.utils.inapp.Purchase;
import com.ae.apps.messagecounter.R;

/**
 * Activity for showing donations, using the DonationsBaseActivity for inapp billing.
 * 
 * @author MidhunHK
 *
 */
public class DonationsActivity extends DonationsBaseActivity {

	private static final String	EXTRA_DATA	= "marmaladespringcat";
	private static final String	TAG			= "DonationsActivity";
	private static final String	SKU_SMALL	= "product_test";
	private static final String	SKU_MEDIUM	= "product_medium";
	private static final String	SKU_LARGE	= "product_large";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displayHomeAsUp();
		
		setToolbarTitle(getString(R.string.menu_donate));

		// Find the Donate buttons and setup the click listeners
		Button btnDonateSmall = (Button) findViewById(R.id.btnDonateSample);
		
		btnDonateSmall.setOnClickListener(mDonateButtonClickListener);

	}
	
	// The click listener for the donation buttons on the page
	View.OnClickListener mDonateButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Identify the donation type
			String productCode = null;
			switch(v.getId()){
			case R.id.btnDonateSample:
				productCode = SKU_SMALL;
				break;
				default:
					productCode = SKU_SMALL;
			}
			
			// Invoke the purchase flow method from the DonationsBaseActivity
			launchPurchaseFlow(productCode, EXTRA_DATA);
		}
	};
	
	@Override
	protected int getToolbarResourceId() {
		return R.id.toolbar;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_donate;
	}

	@Override
	protected String getBase64PublicKey() {
		String base64EncodedPublicKey = getString(R.string.app_lic_cat);
		return base64EncodedPublicKey;
	}

	@Override
	protected boolean checkPurchaseResponse(Purchase info) {
		// Check if we have a valid SKU to perform the inapp purchase
		String sku = info.getSku();
		if (SKU_SMALL.equals(sku) || SKU_MEDIUM.equals(sku) || SKU_LARGE.equals(sku)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPurchaseConsumeFinished(Purchase purchase, IabResult result) {
		if(result.isSuccess()){
			Log.d(TAG, "thank u");
			Toast.makeText(getApplicationContext(), " Thank You for the donation.", Toast.LENGTH_SHORT).show();
		} else {
			// handle error
			Toast.makeText(getApplicationContext(), result.getResponse(), Toast.LENGTH_SHORT).show();
		}
	}

}
