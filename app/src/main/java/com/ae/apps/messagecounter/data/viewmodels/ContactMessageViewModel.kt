package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import com.ae.apps.common.managers.ContactManager
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.common.managers.contact.AeContactManager
import com.ae.apps.common.utils.ValueComparator
import com.ae.apps.common.vo.ContactMessageVo
import com.ae.apps.messagecounter.data.business.COLUMN_NAME_ADDRESS
import com.ae.apps.messagecounter.data.business.SMS_TABLE_MINIMAL_PROJECTION
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel that processes and holds the data for showing Sent and Received message counts
 */
class ContactMessageViewModel : ViewModel() {

    private var mSentMessageContacts: MutableLiveData<List<ContactMessageVo>> = MutableLiveData()

    private var mReceivedMessageContacts: MutableLiveData<List<ContactMessageVo>> = MutableLiveData()

    fun getSentMessageContacts(): LiveData<List<ContactMessageVo>> = mSentMessageContacts

    fun getReceivedMessageContacts(): LiveData<List<ContactMessageVo>> = mReceivedMessageContacts

    fun getContactMessageData(context: Context) {
        if (null == mSentMessageContacts.value || null == mReceivedMessageContacts.value) {
            computeMessageCounts(context)
        }
    }

    private fun computeMessageCounts(context: Context) {
        val contactManager: AeContactManager = ContactManager.Builder(context.contentResolver, context.resources)
                .addContactsWithPhoneNumbers(true)
                .build()

        doAsync {
            contactManager.fetchAllContacts()

            val sentMessages = getContactMessageCountMap(context, contactManager, SMSManager.SMS_URI_SENT)
            val receivedMessages = getContactMessageCountMap(context, contactManager, SMSManager.SMS_URI_INBOX)

            val sentContactMessages = getContactMessagesList(contactManager, sentMessages)
            val receivedContactMessages = getContactMessagesList(contactManager, receivedMessages)

            mSentMessageContacts.postValue(sentContactMessages)
            mReceivedMessageContacts.postValue(receivedContactMessages)
        }
    }

    private fun getContactMessagesList(contactManager: AeContactManager,
                                       messageCountMap: MutableMap<String, Int>): List<ContactMessageVo> {
        val contactMessages: MutableList<ContactMessageVo> = ArrayList()
        messageCountMap.onEach {
            val contactMessageVo = ContactMessageVo()
            contactMessageVo.messageCount = it.value
            contactMessageVo.contactVo = contactManager.getContactInfo(it.key)
            contactMessageVo.photo = contactManager.getContactPhoto(it.key)
            contactMessages.add(contactMessageVo)
        }
        return contactMessages
    }

    /**
     * Returns a sorted map of ContactId and MessageCount for the URI specified
     */
    private fun getContactMessageCountMap(context: Context,
                                          contactManager: AeContactManager,
                                          uri: String): MutableMap<String, Int> {
        val addressMessageCount = mutableMapOf<String, Int>()
        val cursor = context.contentResolver.query(
                Uri.parse(uri),
                SMS_TABLE_MINIMAL_PROJECTION,
                null, null, null)
        if (cursor.count > 0 && cursor.moveToFirst()) {
            val addressIndex: Int = cursor.getColumnIndex(COLUMN_NAME_ADDRESS)
            do {
                val address = cursor.getString(addressIndex)
                // Converting an address from SMS table to corresponding ContactID from Contacts table
                val contactId: String? = contactManager.getContactIdFromAddress(address)
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
        cursor.close()
        return sort(addressMessageCount)
    }

    private fun sort(source: MutableMap<String, Int>): MutableMap<String, Int> {
        val sortedMap = TreeMap<String, Int>(ValueComparator(source))
        sortedMap.putAll(source)
        return sortedMap
    }

}