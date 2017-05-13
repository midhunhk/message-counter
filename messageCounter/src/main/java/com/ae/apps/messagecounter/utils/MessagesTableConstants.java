package com.ae.apps.messagecounter.utils;

/**
 * Created by user on 5/5/2017.
 */

public interface MessagesTableConstants {

    String COLUMN_NAME_PROTOCOL = "protocol";
    String COLUMN_NAME_ID = "_id";
    String COLUMN_NAME_DATE = "date";
    String COLUMN_NAME_PERSON = "person";
    String[] SMS_TABLE_PROJECTION = new String[]{
            COLUMN_NAME_ID, COLUMN_NAME_DATE, COLUMN_NAME_PERSON, COLUMN_NAME_PROTOCOL
    };
}
