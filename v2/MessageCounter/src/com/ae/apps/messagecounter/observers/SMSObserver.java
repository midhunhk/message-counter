package com.ae.apps.messagecounter.observers;

import java.util.Calendar;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * This class will observe the SMS content provider for any changes
 * 
 * @author Midhun
 * 
 */
public class SMSObserver extends ContentObserver {

	private ContentResolver	mContentResolver	= null;
	private Uri				observableUri		= null;

	public SMSObserver(Handler handler, ContentResolver contentResolver, Uri uriToObserve) {
		super(handler);
		mContentResolver = contentResolver;
		observableUri = uriToObserve;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = mContentResolver.query(observableUri, null, null, null, null);
		if (cursor.moveToNext()) {
			String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
			/*
			 * String address = cursor.getString(cursor.getColumnIndex("address")); String person =
			 * cursor.getString(cursor.getColumnIndex("person")); String status =
			 * cursor.getString(cursor.getColumnIndex("status")); String type =
			 * cursor.getString(cursor.getColumnIndex("type")); String dateSent =
			 * cursor.getString(cursor.getColumnIndex("date_sent"));
			 */

			Log.d("SMSObserver", "Protocol is " + protocol);
			/*
			 * Log.d("SMSObserver", "address is " + address); Log.d("SMSObserver", "person is " + person);
			 * Log.d("SMSObserver", "status is " + status); Log.d("SMSObserver", "type is " + type);
			 * Log.d("SMSObserver", "dateSent is " + dateSent);
			 */
			// protocol will be null for sent messages
			if (protocol == null) {
				// Message is sent just now
				// TODO : Update the database with this time
				Log.d("SendSMSObserver", " An SMS was sent at " + Calendar.getInstance().getTime().getTime());
			}
		}
	}

}
