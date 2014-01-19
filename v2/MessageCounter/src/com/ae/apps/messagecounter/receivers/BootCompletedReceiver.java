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

package com.ae.apps.messagecounter.receivers;

import com.ae.apps.messagecounter.services.SMSObserverService;
import com.ae.apps.messagecounter.utils.AppConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This Receiver should be invoked on boot complete, so that we can start the SMSObserverService
 * 
 * @author Midhun
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String	TAG	= "BootCompletedReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		// We can't wait on the main thread as it would be blocked if we wait for too long
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "onBootCompleted. Waiting to run service");
				try {
					// Lets wait some time before starting the service to be fair to other processes on startup
					Thread.sleep(AppConstants.SERVICE_START_DELAY);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
				Log.d(TAG, "Starting service now");
				context.startService(new Intent(context, SMSObserverService.class));
			}
		}).start();
	}

}
