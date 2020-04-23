package com.ae.apps.messagecounter.smsbackup

    var COLUMN_NAME_PROTOCOL = "protocol"
    var COLUMN_NAME_ID = "_id"
    var COLUMN_NAME_DATE = "date"
    var COLUMN_NAME_PERSON = "person"
    var COLUMN_NAME_BODY = "body"
    var COLUMN_NAME_ADDRESS = "address"

    var SMS_TABLE_PROJECTION = arrayOf(
            COLUMN_NAME_ID,
            COLUMN_NAME_DATE,
            COLUMN_NAME_PERSON,
            COLUMN_NAME_BODY,
            COLUMN_NAME_PROTOCOL,
            COLUMN_NAME_ADDRESS)