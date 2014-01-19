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

package com.ae.apps.messagecounter.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.messagecounter.observers.SMSObserver;

/**
 * This service will be used to register a Content Observer for Sent SMS
 * 
 * @author Midhun
 * 
 */
public class SMSObserverService extends Service {

	@Override
	public void onCreate() {
		Uri smsUri = Uri.parse(SMSManager.SMS_URI_ALL);
		// The observer will receive a handler created in the UI thread
		SMSObserver observer = new SMSObserver(new Handler(), this.getContentResolver(), smsUri);

		Log.d("SMSObserverService", "registering content observer");
		// Register the content observer
		this.getContentResolver().registerContentObserver(smsUri, true, observer);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Request the OS to restart this service if killed due to low memory when possible
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
