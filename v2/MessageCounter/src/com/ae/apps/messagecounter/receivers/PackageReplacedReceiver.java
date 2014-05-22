package com.ae.apps.messagecounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ae.apps.messagecounter.services.SMSObserverService;
import com.ae.apps.messagecounter.utils.AppConstants;

/**
 * Receiver when a package is replaced. We need to restart the service if it is enabled after the application is
 * updated. Before an update, the service would've been killed
 * 
 * @author Midhunhk
 * 
 */
public class PackageReplacedReceiver extends BroadcastReceiver {

	private static final String	TAG	= "PackageReplacedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String replacedPackage = intent.getDataString();
		String currentPackage = context.getPackageName();

		Log.d(TAG, "A package was replaced ");
		if (null != replacedPackage && replacedPackage.contains(currentPackage)) {
			// We need to check if the sent count is enabled before starting the service
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			boolean startService = preferences.getBoolean(AppConstants.PREF_KEY_ENABLE_SENT_COUNT, false);

			if (startService) {
				// Run the service
				Log.d(TAG, "Re-Starting service now");
				context.startService(new Intent(context, SMSObserverService.class));
			}
		}

	}

}
