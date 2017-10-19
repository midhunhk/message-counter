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

package com.ae.apps.messagecounter.utils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.telephony.SmsMessage;

import com.ae.apps.common.utils.ValueComparator;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.messagecounter.models.Cycle;
import com.ae.apps.messagecounter.models.Message;
import com.ae.apps.messagecounter.vo.GraphData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility methods for MessageCounter
 *
 * @author Midhun
 */
public class MessageCounterUtils {

    private static final String NO_LIMIT_SET = "-1";
    private static SimpleDateFormat DATE_INDEX_FORMAT = new SimpleDateFormat("yyMMdd", Locale.getDefault());
    private static SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("dd MMM", Locale.getDefault());

    /**
     * Sorts a map using the value rather than key
     *
     * @param source source map
     * @return sorted map
     */
    public static Map<String, Integer> sortThisMap(Map<String, Integer> source) {
        ValueComparator<String, Integer> comparator = new ValueComparator(source);
        Map<String, Integer> sortedMap = new TreeMap<>(comparator);

        sortedMap.putAll(source);
        return sortedMap;
    }

    /**
     * Converts a list into percentages
     *
     * @param messageVos         list of message vos
     * @param totalMessagesCount total messages
     * @param maxRowsForChart    max number of rows
     * @param skipOthersCount    flag for skipping others count
     * @return
     */
    public static GraphData getMessageCountDegrees(List<ContactMessageVo> messageVos, int totalMessagesCount,
                                                   int maxRowsForChart, boolean skipOthersCount) {
        int messageCount;
        float tempCounter = 0;

        // Create a copy of the source list and sort it
        List<ContactMessageVo> sortedList = new ArrayList<ContactMessageVo>(messageVos);
        Collections.copy(sortedList, messageVos);
        Collections.sort(sortedList);

        String contactName;
        Float percentInDegrees;
        ContactMessageVo messageVo;

        Map<String, Float> messageCountMap = new HashMap<String, Float>();
        ValueComparator comparator = new ValueComparator(messageCountMap);
        Map<String, Float> sortedMap = new TreeMap<String, Float>(comparator);

        int loopLimit = sortedList.size();
        if (maxRowsForChart > 1 && loopLimit > maxRowsForChart) {
            loopLimit = maxRowsForChart - 1;
        }

        int messageCountFromRealContacts = 0;
        for (ContactMessageVo contactMessageVo : sortedList) {
            messageCountFromRealContacts += contactMessageVo.getMessageCount();
        }

        if (skipOthersCount) {
            totalMessagesCount = messageCountFromRealContacts;
        }

        for (int index = 0; index < loopLimit; index++) {
            messageVo = sortedList.get(index);
            messageCount = messageVo.getMessageCount();
            if (messageVo.getContactVo() != null && messageVo.getContactVo().getName() != null) {
                contactName = messageVo.getContactVo().getName() + " (" + messageCount + ")";
            } else {
                contactName = "Contact " + index + " (" + messageCount + ")";
            }

            // convert to degree
            percentInDegrees = 360f * messageCount / totalMessagesCount;
            messageCountMap.put(contactName, percentInDegrees);

            tempCounter += messageCount;
        }

        // Calculate the data for the others row
        float balanceMessageCount = totalMessagesCount - tempCounter;
        // Show it only if necessary
        if (balanceMessageCount > 0) { // && sortedList.size() > maxRowsForChart) {
            percentInDegrees = 360f * balanceMessageCount / totalMessagesCount;
            messageCountMap.put("Others (" + (int) balanceMessageCount + ")", percentInDegrees);
        }

        // Get the data from the map and put inside the Graph data object
        sortedMap.putAll(messageCountMap);

        String[] labels = new String[sortedMap.size()];
        float[] values = new float[sortedMap.size()];
        int i = 0;
        for (Float f : sortedMap.values()) {
            values[i] = f;
            i++;
        }

        // Create a new GraphData object to hold the graph data
        GraphData data = new GraphData();
        data.setLabels(sortedMap.keySet().toArray(labels));
        data.setValueInDegrees(values);

        return data;
    }

    /**
     * Returns the index from the a given date
     *
     * @param date date
     * @return index
     */
    public static long getIndexFromDate(Date date) {
        return Long.parseLong(DATE_INDEX_FORMAT.format(date));
    }

    public static CharSequence getDisplayDateString(Date cycleStartDate) {
        return DATE_DISPLAY_FORMAT.format(cycleStartDate);
    }

    /**
     * Calculates the number of messages in a multi part message based on the messageBody
     * @param messageBody the message body
     * @return message count
     */
    public static int getMessageCount(String messageBody){
        return SmsMessage.calculateLength(messageBody, false)[0];
    }

    /**
     * Returns the cycle end date. By default, cycle duration will be a month
     *
     * @param startDate start date
     * @return end date
     */
    public static Date getCycleEndDate(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, +1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * Returns the previous cycle start date
     *
     * @param startDate start date
     * @return previous cycle start date
     */
    public static Date getPrevCycleStartDate(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * Returns the start of the week
     *
     * @return
     */
    public static Date getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return calendar.getTime();
    }

    public static int getMessageLimitValue(SharedPreferences preferences) {
        String rawVal = preferences.getString(AppConstants.PREF_KEY_MESSAGE_LIMIT_VALUE, NO_LIMIT_SET);
        int limit;
        try {
            limit = Integer.valueOf(rawVal);
        } catch (NumberFormatException e) {
            limit = AppConstants.DEFAULT_MESSAGE_LIMIT;
        }
        return limit;
    }

    public static Date getCycleStartDate(SharedPreferences preferences) {
        int cycleStart = Integer.valueOf(preferences.getString(AppConstants.PREF_KEY_CYCLE_START_DATE,
                AppConstants.DEFAULT_CYCLE_START_DATE));
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DATE) < cycleStart) {
            calendar.add(Calendar.MONTH, -1);
        }
        calendar.set(Calendar.DATE, cycleStart);
        return calendar.getTime();
    }

    /**
     * Get count of messages sent in a cycle
     *
     * @param cycleStartDate cycle start date
     * @return
     */
    public static Cycle getCycleSentCount(Date cycleStartDate) {
        Date cycleEndDate = MessageCounterUtils.getCycleEndDate(cycleStartDate);
        long cycleStartIndex = MessageCounterUtils.getIndexFromDate(cycleStartDate);
        long cycleEndIndex = MessageCounterUtils.getIndexFromDate(cycleEndDate);
        return new Cycle(cycleStartIndex, cycleEndIndex);
    }

    /**
     * Returns the dates in a startDate - endDate format
     *
     * @param startDate start date
     * @return duration date string
     */
    public static String getDurationDateString(Date startDate) {
        Date endDate = MessageCounterUtils.getCycleEndDate(startDate);
        return MessageCounterUtils.getDisplayDateString(startDate) + " - "
                + MessageCounterUtils.getDisplayDateString(endDate);
    }

    public static String getContentFromXml(Resources resources, int fileId) {
        XmlResourceParser parser = resources.getXml(fileId);

        return parser.getText();
    }

    public static Message getMessageFromCursor(Cursor cursor) {
        final String messageId = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_ID));
        final String protocol = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_PROTOCOL));
        final String sentTime = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_DATE));
        final String body = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_BODY));
        final String address = cursor.getString(cursor.getColumnIndex(MessagesTableConstants.COLUMN_NAME_ADDRESS));

        Message message = new Message();
        message.setId(messageId);
        message.setBody(body);
        message.setDate(sentTime);
        message.setProtocol(protocol);
        message.setAddress(address);

        // Calculate the messages count for multipart messages
        int messagesCount = 1;
        try{
            messagesCount = getMessageCount(body);
        } catch(Exception e){
            // Skip any exceptions
        }
        message.setMessagesCount(messagesCount);

        return message;
    }
}