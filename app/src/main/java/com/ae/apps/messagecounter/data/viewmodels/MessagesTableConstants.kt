package com.ae.apps.messagecounter.data.viewmodels

import android.database.Cursor
import com.ae.apps.messagecounter.data.models.Message
import com.ae.apps.messagecounter.data.repositories.getMessageCount


var COLUMN_NAME_PROTOCOL = "protocol"
var COLUMN_NAME_ID = "_id"
var COLUMN_NAME_DATE = "date"
var COLUMN_NAME_PERSON = "person"
var COLUMN_NAME_BODY = "body"
var COLUMN_NAME_ADDRESS = "address"

var SMS_TABLE_PROJECTION = arrayOf(COLUMN_NAME_ID,
        COLUMN_NAME_DATE,
        COLUMN_NAME_PERSON,
        COLUMN_NAME_BODY,
        COLUMN_NAME_PROTOCOL,
        COLUMN_NAME_ADDRESS)

fun getMessageFromCursor(cursor: Cursor): Message {
    val messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID))
    val protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL))
    val sentTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATE))
    val body = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BODY))
    val address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS))


    // Calculate the messages count for multipart messages
    var messagesCount = 1
    try {
        messagesCount = getMessageCount(body)
    } catch (e: Exception) {
        // Skip any exceptions
    }

    return Message(messageId, messagesCount, body, sentTime, protocol, address)
}