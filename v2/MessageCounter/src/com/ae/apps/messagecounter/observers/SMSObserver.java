package com.ae.apps.messagecounter.observers;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

/**
 * This class will observe the SMS content provider for any changes
 * 
 * @author Midhun
 * 
 */
public class SMSObserver extends ContentObserver {

	private Uri		observableUri	= null;
	private Context	mContext		= null;
	private String	mLastMessageId	= null;

	public SMSObserver(Handler handler, Context context, Uri uriToObserve) {
		super(handler);
		mContext = context;
		observableUri = uriToObserve;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = mContext.getContentResolver().query(observableUri, null, null, null, null);
		if (cursor.moveToNext()) {
			// We need the protocol and the message _id
			String messageId = cursor.getString(cursor.getColumnIndex("_id"));
			String protocol = cursor.getString(cursor.getColumnIndex("protocol"));

			// See if this message was processed earlier
			boolean isNewMessage = (mLastMessageId == null || !mLastMessageId.equals(messageId));

			// protocol will be null for sent messages
			if (protocol == null && isNewMessage) {
				// A Message was sent just now
				Date date = Calendar.getInstance().getTime();
				CounterDataBaseAdapter counterDataBase = new CounterDataBaseAdapter(mContext);
				long today = MessageCounterUtils.getIndexFromDate(date);

				// Add an entry into the database
				counterDataBase.addMessageSentCounter(today);
				counterDataBase.close();

				// Store this message id incase we get multiple callbacks for the same id
				Log.d("SendSMSObserver", " An SMS was sent at " + date.getTime() + " with id " + messageId);
				mLastMessageId = messageId;
			}
		}
		cursor.close();
	}

}
