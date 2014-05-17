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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.SharedPreferences;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.utils.IntegerComparator;
import com.ae.apps.common.utils.ValueComparator;
import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.vo.ContactMessageVo;
import com.ae.apps.messagecounter.vo.GraphData;

/**
 * Utility methods for MessageCounter
 * 
 * @author Midhun
 * 
 */
public class MessageCounterUtils {

	private static final String		NO_LIMIT_SET		= "-1";
	private static SimpleDateFormat	DATE_INDEX_FORMAT	= new SimpleDateFormat("yyMMdd", Locale.getDefault());
	private static SimpleDateFormat	DATE_DISPLAY_FORMAT	= new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

	/**
	 * Sorts a map using the value rather than key
	 * 
	 * @param source
	 * @return
	 */
	public static Map<String, Integer> sortThisMap(Map<String, Integer> source) {
		IntegerComparator comparator = new IntegerComparator(source);
		Map<String, Integer> sortedMap = new TreeMap<String, Integer>(comparator);
		sortedMap.putAll(source);
		return sortedMap;
	}

	/**
	 * Converts a list into percentages
	 * 
	 * @param messageVos
	 * @param totalMessagesCount
	 * @param maxRowsForChart
	 * @return
	 */
	public static GraphData getMessageCountDegrees(List<ContactMessageVo> messageVos, int totalMessagesCount,
			int maxRowsForChart, boolean skipOthersCount) {
		int messageCount = 0;
		float tempCounter = 0;

		// Create a copy of the source list and sort it
		List<ContactMessageVo> sortedList = new ArrayList<ContactMessageVo>(messageVos);
		Collections.copy(sortedList, messageVos);
		Collections.sort(sortedList);

		String contactName = null;
		Float percentInDegrees = null;
		ContactMessageVo messageVo = null;

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
	 * Returns a list of ContactMessageVos
	 * 
	 * @param manager
	 * @param sortedMessageMap
	 * @param messageMap
	 * @return
	 */
	public static List<ContactMessageVo> getContactMessageList(ContactManager manager,
			Map<String, Integer> sortedMessageMap, Map<String, Integer> messageMap) {
		// Collate the ContactVo, photo bitmap and message count and create the data for the list
		Integer messageCount = null;
		ContactMessageVo contactMessageVo = null;
		Set<String> contactIdsKeySet = sortedMessageMap.keySet();
		List<ContactMessageVo> contactMessageList = new ArrayList<ContactMessageVo>();
		for (String contactId : contactIdsKeySet) {
			messageCount = messageMap.get(contactId);
			if (messageCount != null) {
				// create a new ContactMessageVo object with the data that we have
				contactMessageVo = new ContactMessageVo();
				contactMessageVo.setContactVo(manager.getContactInfo(contactId));
				contactMessageVo.setMessageCount(messageCount);
				contactMessageVo.setPhoto(manager.getContactPhoto(contactId));
				contactMessageList.add(contactMessageVo);
			}
		}
		return contactMessageList;
	}

	/**
	 * Returns a mapping of address and total message count
	 * 
	 * @param manager
	 * @param messageSendersMap
	 * @return
	 */
	public static Map<String, Integer> convertAddressToContact(ContactManager manager,
			Map<String, Integer> messageSendersMap) {
		// mapping of contactId with message count
		Map<String, Integer> contactsMap = new HashMap<String, Integer>();

		String contactId = null;
		Integer messageCount = null;
		Set<String> addressesKeySet = messageSendersMap.keySet();
		for (String address : addressesKeySet) {
			contactId = manager.getContactIdFromAddress(address);
			// contactId will be null for address not in contact list
			if (contactId != null) {
				messageCount = messageSendersMap.get(address);
				if (contactsMap.containsKey(contactId)) {
					// If contactId already exists, update the message count
					messageCount = messageCount + contactsMap.get(contactId);
				}
				contactsMap.put(contactId, messageCount);
			}
		}
		return contactsMap;
	}

	/**
	 * Returns the index from the a given date
	 * 
	 * @param date
	 * @return
	 */
	public static long getIndexFromDate(Date date) {
		return Long.parseLong(DATE_INDEX_FORMAT.format(date));
	}

	public static CharSequence getDisplayDateString(Date cycleStartDate) {
		return DATE_DISPLAY_FORMAT.format(cycleStartDate);
	}

	/**
	 * Returns the cycle end date. By default, cycle duration will be a month
	 * 
	 * @param startDate
	 * @return
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
	 * @param startDate
	 * @return
	 */
	public static Date getPrevCycleStartDate(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}

	public static int getMessageLimitValue(SharedPreferences preferences) {
		String rawVal = preferences.getString(AppConstants.PREF_KEY_MESSAGE_LIMIT_VALUE, NO_LIMIT_SET);
		int limit = AppConstants.DEFAULT_MESSAGE_LIMIT;
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

	public static int getCycleSentCount(CounterDataBaseAdapter counterDataBase, Date cycleStartDate) {
		Date cycleEndDate = MessageCounterUtils.getCycleEndDate(cycleStartDate);
		long cycleStartIndex = MessageCounterUtils.getIndexFromDate(cycleStartDate);
		long cycleEndIndex = MessageCounterUtils.getIndexFromDate(cycleEndDate);
		return counterDataBase.getTotalSentCountBetween(cycleStartIndex, cycleEndIndex);
	}

	/**
	 * Returns the dates in a startDate - endDate format
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDurationDateString(Date startDate) {
		Date endDate = MessageCounterUtils.getCycleEndDate(startDate);
		return MessageCounterUtils.getDisplayDateString(startDate) + " - "
				+ MessageCounterUtils.getDisplayDateString(endDate);
	}
}