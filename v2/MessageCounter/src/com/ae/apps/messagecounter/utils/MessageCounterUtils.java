/*
 * Copyright 2013 Midhun Harikumar
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.utils.IntegerComparator;
import com.ae.apps.common.utils.ValueComparator;
import com.ae.apps.messagecounter.vo.ContactMessageVo;
import com.ae.apps.messagecounter.vo.GraphData;

/**
 * Utility methods for MessageCounter
 * 
 * @author Midhun
 * 
 */
public class MessageCounterUtils {

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
			int maxRowsForChart) {
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

		for (int index = 0; index < loopLimit; index++) {
			messageVo = sortedList.get(index);
			messageCount = messageVo.getMessageCount();
			if (messageVo.getContactVo() != null && messageVo.getContactVo().getName() != null) {
				contactName = messageVo.getContactVo().getName() + " (" + (int) messageCount + ")";
			} else {
				contactName = "Contact " + index + " (" + (int) messageCount + ")";
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
				// Time to create a new ContactMessageVo object with the data that we have
				contactMessageVo = new ContactMessageVo();
				contactMessageVo.setContactVo(manager.getContactInfo(contactId));
				contactMessageVo.setMessageCount(messageCount);
				contactMessageVo.setPhoto(manager.getContactPhoto(contactId));
				contactMessageList.add(contactMessageVo);
			}
		}
		return contactMessageList;
	}

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
}