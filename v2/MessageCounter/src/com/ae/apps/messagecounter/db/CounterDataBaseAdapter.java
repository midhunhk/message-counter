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
package com.ae.apps.messagecounter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ae.apps.common.db.DataBaseHelper;

/**
 * Access to the repository
 * 
 * @author Midhun
 * 
 */
public class CounterDataBaseAdapter extends DataBaseHelper {

	private static String			DATABASE_NAME			= "db_message_counter";
	private static final int		DATABASE_VERSION		= 1;
	private static final String		TABLE_COUNTER			= "tbl_sms_counter";

	public static final String		KEY_DATE				= "date_index";
	public static final String		KEY_COUNT				= "sent_count";

	/* The SQL to create the table */
	private static final String		COUNTER_TABLE_CREATE	= "CREATE TABLE " + TABLE_COUNTER + " (" + KEY_DATE
																	+ " NUMERIC PRIMARY KEY, " + KEY_COUNT
																	+ " NUMERIC NOT NULL);";
	/* Projection for retrieving the data */
	private static final String[]	DATA_PROJECTION			= new String[] { KEY_DATE, KEY_COUNT };

	private static final String[]	CREATE_TABLES_SQL		= new String[] { COUNTER_TABLE_CREATE };

	public CounterDataBaseAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, CREATE_TABLES_SQL);
	}

	/**
	 * Fetch all the rows
	 * 
	 * @return
	 */
	public Cursor getAllCountInfo() {
		return query(TABLE_COUNTER, DATA_PROJECTION, null, null, null, null, null);
	}

	public int getCountValueForDay(long dateIndex) {
		int returnValue = 0;
		String[] args = { dateIndex + "" };
		Cursor cursor = query(TABLE_COUNTER, DATA_PROJECTION, KEY_DATE + " = ? ", args, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			returnValue = -1;
		} else {
			cursor.moveToNext();
			returnValue = cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
			cursor.close();
		}

		return returnValue;
	}

	/**
	 * 
	 * @param 
	 * @return
	 */
	public long addMessageSentCounter(long dateIndex) {
		int currentCount = getCountValueForDay(dateIndex);
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DATE, dateIndex);
		long result = 0;

		if (currentCount == -1) {
			// no rows exist for this day, so insert a new row
			currentCount = 1;
			contentValues.put(KEY_COUNT, currentCount);
			result = insert(TABLE_COUNTER, contentValues);
		} else {
			// update the count for this day
			currentCount++;
			contentValues.put(KEY_COUNT, currentCount);
			String[] whereArgs = { dateIndex + "" };
			result = update(TABLE_COUNTER, contentValues, KEY_DATE + " = ? ", whereArgs);
		}
		return result;
	}

	/**
	 * 
	 * @param startDateIndex
	 * @return
	 */
	public int getTotalSentCountSinceDate(long startDateIndex) {
		int count = 0;
		String[] selectionArgs = { startDateIndex + "" };
		Cursor cursor = rawQuery(
				"SELECT SUM(" + KEY_COUNT + ") FROM " + TABLE_COUNTER + " WHERE " + KEY_DATE + " >= ?", selectionArgs);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

}
