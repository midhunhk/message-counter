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
import android.database.sqlite.SQLiteDatabase;

import com.ae.apps.common.db.DataBaseHelper;

/**
 * Class that helps access the database
 *
 * @author Midhun
 */
public class CounterDataBaseAdapter extends DataBaseHelper {

    private static volatile CounterDataBaseAdapter sInstance;
    private static final Object sMutex = new Object();

    /**
     * A thread safe factory method that returns the only instance of the CounterDataBaseAdapter
     *
     * @param context context required by the DataBaseHelper
     * @return
     */
    public static CounterDataBaseAdapter getInstance(Context context){
        if(null == sInstance){
            synchronized (sMutex){
                if (null == sInstance){
                    sInstance = new CounterDataBaseAdapter(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * Create an instance of CounterDataBaseAdapter
     *
     * @param context context
     */
    private CounterDataBaseAdapter(Context context) {
        super(context, CounterDataBaseConstants.DATABASE_NAME, null,
                CounterDataBaseConstants.DATABASE_VERSION, CounterDataBaseConstants.CREATE_TABLES_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Fetch all the data
     *
     * @return cursor
     */
    public Cursor getAllCountInfo() {
        return query(CounterDataBaseConstants.MESSAGE_COUNTER_TABLE,
                CounterDataBaseConstants.MESSAGE_COUNTER_COLUMNS,
                null, null, null, null, null);
    }

    /**
     * Returns the count for a specific day
     *
     * @param dateIndex date index
     * @return message count for date index
     */
    public int getCountValueForDay(long dateIndex) {
        int returnValue;
        String[] args = {String.valueOf(dateIndex)};
        Cursor cursor = query(CounterDataBaseConstants.MESSAGE_COUNTER_TABLE, CounterDataBaseConstants.MESSAGE_COUNTER_COLUMNS,
                CounterDataBaseConstants.MESSAGE_COUNTER_DATE_INDEX + " = ? ", args, null, null, null);
        if (null == cursor || cursor.getCount() == 0) {
            returnValue = -1;
        } else {
            cursor.moveToNext();
            returnValue = cursor.getInt(cursor.getColumnIndex(CounterDataBaseConstants.MESSAGE_COUNTER_SENT_COUNT));
        }

        if(null != cursor) cursor.close();

        return returnValue;
    }

    /**
     * Update the messages sent count represented by a dateIndex.
     *
     * If no messages have been sent for this date, a new row would be inserted with
     * messageCount.
     * If an existing row is found, the current count would be updated with the
     * messageCount.
     *
     * @param dateIndex date index
     * @param messageCount messages count to add
     * @return insert or update status
     */
    public long addMessageSentCounter(long dateIndex, int messageCount) {
        if(messageCount <= 0){
            return -1;
        }

        int currentCount = getCountValueForDay(dateIndex);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterDataBaseConstants.MESSAGE_COUNTER_DATE_INDEX, dateIndex);
        long result;

        if (currentCount == -1) {
            // no rows exist for this day, so insert a new row
            contentValues.put(CounterDataBaseConstants.MESSAGE_COUNTER_SENT_COUNT, messageCount);
            result = insert(CounterDataBaseConstants.MESSAGE_COUNTER_TABLE, contentValues);
        } else {
            // update the count for this day
            int updatedCount = currentCount + messageCount;
            contentValues.put(CounterDataBaseConstants.MESSAGE_COUNTER_SENT_COUNT, updatedCount);
            String[] whereArgs = {dateIndex + ""};
            result = update(CounterDataBaseConstants.MESSAGE_COUNTER_TABLE, contentValues,
                    CounterDataBaseConstants.MESSAGE_COUNTER_DATE_INDEX + " = ? ", whereArgs);
        }
        return result;
    }

    /**
     * Returns the sum of messages sent since a startDay
     *
     * @param startDateIndex start date index
     * @return total messages sent since start day
     */
    public int getTotalSentCountSinceDate(long startDateIndex) {
        int count = 0;
        String[] selectionArgs = {startDateIndex + ""};
        Cursor cursor = rawQuery(
                "SELECT SUM(" + CounterDataBaseConstants.MESSAGE_COUNTER_SENT_COUNT + ") FROM "
                        + CounterDataBaseConstants.MESSAGE_COUNTER_TABLE
                        + " WHERE " + CounterDataBaseConstants.MESSAGE_COUNTER_DATE_INDEX + " >= ?", selectionArgs);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    /**
     * Returns the sum of message sent between two dates
     *
     * @param startDateIndex start date index
     * @param endDateIndex   end date index
     * @return messages sent between the dates
     */
    public int getTotalSentCountBetween(long startDateIndex, long endDateIndex) {
        int count = 0;
        String[] selectionArgs = {startDateIndex + "", endDateIndex + ""};
        Cursor cursor = rawQuery("SELECT SUM(" + CounterDataBaseConstants.MESSAGE_COUNTER_SENT_COUNT + ") FROM "
                + CounterDataBaseConstants.MESSAGE_COUNTER_TABLE + " WHERE "
                + CounterDataBaseConstants.MESSAGE_COUNTER_DATE_INDEX + " BETWEEN ? AND ?", selectionArgs);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

}
