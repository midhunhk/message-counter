/*
 * Copyright 2017 Midhun Harikumar
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

package com.ae.apps.messagecounter.managers;

import android.content.ContentResolver;
import android.content.Context;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.SMSManager;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.messagecounter.utils.MessageCounterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Combines data from ContactsVo and the SMS Manager
 */

public class ContactMessageDataManager {

    private SMSManager smsManager;
    private ContactManager contactManager;

    /**
     * Constructs an instance of this class
     *
     * @param contentResolver
     * @param context
     */
    public ContactMessageDataManager(ContentResolver contentResolver, Context context) {
        final ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = contentResolver;
        config.addContactsWithPhoneNumbers = false;
        contactManager = new ContactManager(config);

        smsManager = new SMSManager(context);
    }

    /**
     * Maps Contact Messages and Contact info into a sorted list
     *
     * @return
     */
    public List<ContactMessageVo> getContactMessagesData() {
        // Get the mapping of address and message count
        // Convert to mapping of contact and message count
        Map<String, Integer> messageSendersMap = convertAddressToContact(smsManager.getUniqueSenders());

        // Sorting the map based on message count
        Map<String, Integer> sortedValuesMap = MessageCounterUtils.sortThisMap(messageSendersMap);

        // Convert this data to a list of ContactMessageVos
        return getContactMessageList(sortedValuesMap, messageSendersMap);
    }

    /**
     * Returns a mapping of address and total message count
     *
     * @param messageSendersMap message sendersmap
     * @return
     */
    private Map<String, Integer> convertAddressToContact(Map<String, Integer> messageSendersMap) {
        // mapping of contactId with message count
        Map<String, Integer> contactsMap = new HashMap<>();

        String contactId;
        Integer messageCount;
        Set<String> addressesKeySet = messageSendersMap.keySet();
        for (String address : addressesKeySet) {
            contactId = contactManager.getContactIdFromAddress(address);
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
     * Returns a list of ContactMessageVos
     *
     * @param sortedMessageMap sorted map
     * @param messageMap       message map
     * @return
     */
    private List<ContactMessageVo> getContactMessageList(Map<String, Integer> sortedMessageMap, Map<String, Integer> messageMap) {
        // Collate the ContactVo, photo bitmap and message count and create the data for the list
        Integer messageCount;
        ContactMessageVo contactMessageVo;
        Set<String> contactIdsKeySet = sortedMessageMap.keySet();
        List<ContactMessageVo> contactMessageList = new ArrayList<ContactMessageVo>();
        if (null != contactIdsKeySet && !contactIdsKeySet.isEmpty()) {
            for (String contactId : contactIdsKeySet) {
                messageCount = messageMap.get(contactId);
                if (messageCount != null) {
                    // create a new ContactMessageVo object with the data that we have
                    contactMessageVo = new ContactMessageVo();
                    contactMessageVo.setContactVo(contactManager.getContactInfo(contactId));
                    contactMessageVo.setMessageCount(messageCount);
                    contactMessageVo.setPhoto(contactManager.getContactPhoto(contactId));
                    contactMessageList.add(contactMessageVo);
                }
            }
        }
        return contactMessageList;
    }
}
