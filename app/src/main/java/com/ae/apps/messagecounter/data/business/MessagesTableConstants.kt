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
package com.ae.apps.messagecounter.data.business

import android.annotation.SuppressLint
import android.database.Cursor
import com.ae.apps.messagecounter.data.models.Message
import com.ae.apps.messagecounter.data.repositories.getMessageCount

const val COLUMN_NAME_PROTOCOL = "protocol"
const val COLUMN_NAME_ID = "_id"
const val COLUMN_NAME_DATE = "date"
const val COLUMN_NAME_PERSON = "person"
const val COLUMN_NAME_BODY = "body"
const val COLUMN_NAME_ADDRESS = "address"

val SMS_TABLE_PROJECTION = arrayOf(COLUMN_NAME_ID,
        COLUMN_NAME_DATE,
        COLUMN_NAME_PERSON,
        COLUMN_NAME_BODY,
        COLUMN_NAME_PROTOCOL,
        COLUMN_NAME_ADDRESS)

val SMS_TABLE_MINIMAL_PROJECTION = arrayOf(COLUMN_NAME_ID,
        COLUMN_NAME_ADDRESS,
        COLUMN_NAME_PERSON)

val SMS_TABLE_ALL_PROJECTION = arrayOf(
        COLUMN_NAME_ID,
        COLUMN_NAME_DATE,
        COLUMN_NAME_PERSON,
        COLUMN_NAME_BODY,
        COLUMN_NAME_PROTOCOL,
        COLUMN_NAME_ADDRESS)

const val SELECT_SENT_MESSAGES_AFTER_DATE = "person is null and protocol is null and $COLUMN_NAME_DATE > ? "
const val SELECT_SENT_MESSAGES_AFTER_LAST = "and _id > ?"
const val SORT_BY_DATE = "$COLUMN_NAME_DATE ASC"

const val DEFAULT_MESSAGE_COUNT = 1
const val DEFAULT_INDEX_TIME_STAMP = "0"

@SuppressLint("Range")
fun getMessageFromCursor(cursor: Cursor): Message {
    val messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID))
    val protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL))
    val sentTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATE))
    val body = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BODY))
    val address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS))

    // Calculate the messages count for multipart messages
    val messagesCount = try {
        getMessageCount(body)
    } catch (e: Exception) {
        // Skip any exceptions and use default value
        DEFAULT_MESSAGE_COUNT
    }
    return Message(messageId, messagesCount, body, sentTime, protocol, address)
}

@SuppressLint("Range")
fun getMessageForBackupFromCursor(cursor: Cursor): Message {
    val messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID))
    val address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS))
    // person is null when they are not in the address book, eg: OTPs, Financial institutions etc
    val person = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PERSON))?: ""
    val protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL))
    val sentTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATE))
    val body = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BODY))

    return Message(messageId, 0, body, sentTime, protocol, address, person)
}