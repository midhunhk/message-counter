package com.ae.apps.messagecounter.utils;

/**
 * Constants related to the Messages Table
 */
public interface MessagesTableConstants {

    String COLUMN_NAME_PROTOCOL = "protocol";
    String COLUMN_NAME_ID = "_id";
    String COLUMN_NAME_DATE = "date";
    String COLUMN_NAME_PERSON = "person";
    String COLUMN_NAME_BODY = "body";
    String COLUMN_NAME_ADDRESS = "address";

    String[] SMS_TABLE_PROJECTION = new String[]{
            COLUMN_NAME_ID, COLUMN_NAME_DATE, COLUMN_NAME_PERSON, COLUMN_NAME_BODY, COLUMN_NAME_PROTOCOL, COLUMN_NAME_ADDRESS
    };
}
