/*
 * Copyright 2018 Midhun Harikumar
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
package com.ae.apps.messagecounter.data.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.sms.utils.SmsApiConstants
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.common.utils.ValueComparator
import com.ae.apps.messagecounter.data.business.COLUMN_NAME_ADDRESS
import com.ae.apps.messagecounter.data.business.SMS_TABLE_MINIMAL_PROJECTION
import com.ae.apps.messagecounter.models.ContactMessageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.system.measureTimeMillis


/**
 * ViewModel that processes and holds the data for showing Sent and Received message counts
 */
class ContactMessageViewModel : ViewModel() {

    private var sentMessageContacts: MutableLiveData<List<ContactMessageInfo>> = MutableLiveData()

    private var receivedMessageContacts: MutableLiveData<List<ContactMessageInfo>> = MutableLiveData()

    fun getSentMessageContacts(): LiveData<List<ContactMessageInfo>> = sentMessageContacts

    fun getReceivedMessageContacts(): LiveData<List<ContactMessageInfo>> = receivedMessageContacts

    fun getContactMessageData(context: Context) {
        if (null == sentMessageContacts.value || null == receivedMessageContacts.value) {
            computeMessageCounts(context)
        }
    }

    private fun computeMessageCounts(context: Context) {
        /*
        val contactManager: AeContactManager = ContactManager.Builder(context.contentResolver, context.resources)
                .addContactsWithPhoneNumbers(true)
                .build()
        */
        val contactsApi: ContactsApiGateway = ContactsApiGatewayImpl.Builder(context)
            .build()
        doAsync {
            val timeToFetchAllContacts = measureTimeMillis {
                contactsApi.initialize(ContactInfoFilterOptions.of(false))
            }
            val timeToProcessData = measureTimeMillis {
                val sentMessages = getContactMessageCountMap(context, contactsApi, SmsApiConstants.URI_SENT) // SMSManager.SMS_URI_SENT
                val receivedMessages = getContactMessageCountMap(context, contactsApi, SmsApiConstants.URI_INBOX) // SMSManager.SMS_URI_INBOX

                val sentContactMessages = getContactMessagesList(contactsApi, sentMessages)
                val receivedContactMessages = getContactMessagesList(contactsApi, receivedMessages)

                sentMessageContacts.postValue(sentContactMessages)
                receivedMessageContacts.postValue(receivedContactMessages)
            }

            Log.d("ContactMessageViewModel", "timeToFetchAllContacts = $timeToFetchAllContacts")
            Log.d("ContactMessageViewModel", "timeToProcessData = $timeToProcessData")
        }
    }

    private fun getContactMessagesList(contactsApi: ContactsApiGateway,
                                       messageCountMap: MutableMap<String, Int>): List<ContactMessageInfo> {
        val contactMessages: MutableList<ContactMessageInfo> = ArrayList()
        messageCountMap.onEach {
            // ContactMessageInfo
            val contactMessageVo = ContactMessageInfo(contactsApi.getContactInfo(it.key), it.value)
            contactMessages.add(contactMessageVo)
        }
        return contactMessages
    }

    /**
     * Returns a sorted map of ContactId and MessageCount for the URI specified
     */
    private fun getContactMessageCountMap(context: Context,
                                          contactsApi: ContactsApiGateway,
                                          uri: String): MutableMap<String, Int> {
        val addressMessageCount = mutableMapOf<String, Int>()
        val cursor = context.contentResolver.query(
                Uri.parse(uri),
                SMS_TABLE_MINIMAL_PROJECTION,
                null, null, null)
        if (null != cursor && cursor.count > 0 && cursor.moveToFirst()) {
            val addressIndex: Int = cursor.getColumnIndex(COLUMN_NAME_ADDRESS)
            do {
                val address = cursor.getString(addressIndex)
                // Converting an address from SMS table to corresponding ContactID from Contacts table
                val contactId: String? = contactsApi.getContactIdFromAddress(address)
                // contactId would be null when the contact is not a saved contact
                if (null != contactId) {
                    if (addressMessageCount.containsKey(contactId)) {
                        val value = addressMessageCount[contactId]
                        val newValue = value!! + 1
                        addressMessageCount[contactId] = newValue
                    } else {
                        addressMessageCount[contactId] = 1
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return sort(addressMessageCount)
    }

    private fun sort(source: MutableMap<String, Int>): MutableMap<String, Int> {
        val sortedMap = TreeMap<String, Int>(ValueComparator(source))
        sortedMap.putAll(source)
        return sortedMap
    }

}