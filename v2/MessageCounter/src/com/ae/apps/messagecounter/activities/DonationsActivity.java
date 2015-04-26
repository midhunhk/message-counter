package com.ae.apps.messagecounter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.utils.inapp.IabHelper;
import com.ae.apps.messagecounter.utils.inapp.IabResult;
import com.ae.apps.messagecounter.utils.inapp.Purchase;

/**
 * Activity for showing donations
 * 
 * @author MidhunHK
 *
 */
public class DonationsActivity extends ToolBarBaseActivity {

	private static final String	EXTRA_DATA	= "marmaladespringcat";
	private static final String	TAG			= "DonationsActivity";
	private static final String	SKU_SMALL	= "product_small";
	private static final String	SKU_MEDIUM	= "product_medium";
	private static final String	SKU_LARGE	= "product_large";
	private static final int	RC_REQUEST	= 2001;
	private IabHelper			mHelper		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displayHomeAsUp();
		
		// Read this from the assets folder
		String base64EncodedPublicKey = "";
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.enableDebugLogging(true);

		// Setup IAB
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "problem in setup inapp " + result);
				}
			}
		});
		
		Button btnDonateSmall = (Button) findViewById(R.id.btnDonateSample);
		
		btnDonateSmall.setOnClickListener(mDonateButtonClickListener);

	}
	
	View.OnClickListener mDonateButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String productCode = null;
			switch(v.getId()){
			case R.id.btnDonateSample:
				productCode = SKU_SMALL;
				break;
			}
			
			if(null != productCode){
				// on button click after selecting a purchase item
				mHelper.launchPurchaseFlow(getParent(), SKU_SMALL, RC_REQUEST, mPurchaseFinishedlistener, EXTRA_DATA);
			}
		}
	};
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedlistener = new IabHelper.OnIabPurchaseFinishedListener() {
		
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			Log.d(TAG, "onpurchasefinished " + result);
			
			if(null == mHelper){
				return;
			}
			
			if(result.isFailure()){
				Log.d(TAG, "purchase failed ");
				Toast.makeText(getApplicationContext(), result.getResponse(), Toast.LENGTH_SHORT).show();
				return;
			}
			
			// If valid SKU
			String sku = info.getSku();
			if(SKU_SMALL.equals(sku) || SKU_MEDIUM.equals(sku) || SKU_LARGE.equals(sku)){
				mHelper.consumeAsync(info, mConsumeFinishedListener);
			}
			
		}
	};
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "onconsumefinished " + result);
			
			if(null == mHelper){
				return;
			}
			
			if(result.isSuccess()){
				Log.d(TAG, "thank u");
				
			} else {
				// handle error
				Toast.makeText(getApplicationContext(), result.getResponse(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d(TAG, "onactivityresult ");

		if (null == mHelper) {
			return;
		}

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled by IABUtil
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onactivityresult handled by IABUtil");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mHelper) {
			mHelper.dispose();
		}
		mHelper = null;
	}

	@Override
	protected int getToolbarResourceId() {
		return R.id.toolbar;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_donate;
	}

}