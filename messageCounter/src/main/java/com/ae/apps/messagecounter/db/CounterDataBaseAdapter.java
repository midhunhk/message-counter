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
 * Class that helps access the database
 *
 * @author Midhun
 */
public class CounterDataBaseAdapter extends DataBaseHelper {


    /**
     * Create an instance of CounterDataBaseAdapter
     *
     * @param context context
     */
    public CounterDataBaseAdapter(Context context) {
        super(context, CounterDataBaseConstants.DATABASE_NAME, null,
                CounterDataBaseConstants.DATABASE_VERSION, CounterDataBaseConstants.CREATE_TABLES_SQL);
    }

    /**
     * Fetch all the data
     *
     * @return cursor
     */
    public Cursor getAllCountInfo() {
        return query(CounterDataBaseConstants.TABLE_COUNTER, CounterDataBaseConstants.DATA_PROJECTION,
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
        String[] args = {dateIndex + ""};
        Cursor cursor = query(CounterDataBaseConstants.TABLE_COUNTER, CounterDataBaseConstants.DATA_PROJECTION,
                CounterDataBaseConstants.KEY_DATE + " = ? ", args, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            returnValue = -1;
        } else {
            cursor.moveToNext();
            returnValue = cursor.getInt(cursor.getColumnIndex(CounterDataBaseConstants.KEY_COUNT));
            cursor.close();
        }

        return returnValue;
    }

    /**
     * Add a count for this dateIndex. If a row is found, the count value is incremented.
     * If not found, a new row will be inserted with the value 1
     *
     * @param dateIndex date index
     * @return insert status
     */
    public long addMessageSentCounter(long dateIndex) {
        int currentCount = getCountValueForDay(dateIndex);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterDataBaseConstants.KEY_DATE, dateIndex);
        long result;

        if (currentCount == -1) {
            // no rows exist for this day, so insert a new row
            currentCount = 1;
            contentValues.put(CounterDataBaseConstants.KEY_COUNT, currentCount);
            result = insert(CounterDataBaseConstants.TABLE_COUNTER, contentValues);
        } else {
            // update the count for this day
            currentCount++;
            contentValues.put(CounterDataBaseConstants.KEY_COUNT, currentCount);
            String[] whereArgs = {dateIndex + ""};
            result = update(CounterDataBaseConstants.TABLE_COUNTER, contentValues,
                    CounterDataBaseConstants.KEY_DATE + " = ? ", whereArgs);
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
                "SELECT SUM(" + CounterDataBaseConstants.KEY_COUNT + ") FROM "
                        + CounterDataBaseConstants.TABLE_COUNTER
                        + " WHERE " + CounterDataBaseConstants.KEY_DATE + " >= ?", selectionArgs);
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
        Cursor cursor = rawQuery("SELECT SUM(" + CounterDataBaseConstants.KEY_COUNT + ") FROM "
                + CounterDataBaseConstants.TABLE_COUNTER + " WHERE "
                + CounterDataBaseConstants.KEY_DATE + " BETWEEN ? AND ?", selectionArgs);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

}
