package com.ae.apps.messagecounter.db;

import android.provider.BaseColumns;

/**
 * Constants for CounterDatabase
 */
public interface CounterDataBaseConstants {
    int DATABASE_VERSION = 2;

    String DATABASE_NAME = "db_message_counter";

    // Message Counter Table
    String MESSAGE_COUNTER_TABLE = "tbl_sms_counter";
    String MESSAGE_COUNTER_DATE_INDEX = "date_index";
    String MESSAGE_COUNTER_SENT_COUNT = "sent_count";

    String MESSAGE_COUNTER_SQL = "CREATE TABLE " + MESSAGE_COUNTER_TABLE + " ("
            + MESSAGE_COUNTER_DATE_INDEX + " NUMERIC PRIMARY KEY, "
            + MESSAGE_COUNTER_SENT_COUNT + " NUMERIC NOT NULL" +
            ");";

    String[] MESSAGE_COUNTER_COLUMNS = new String[]{
            MESSAGE_COUNTER_DATE_INDEX,
            MESSAGE_COUNTER_SENT_COUNT
    };

    // Ignore List Table
    String IGNORE_LIST_TABLE = "tbl_ignore_list";
    String IGNORE_LIST_ID = BaseColumns._ID;
    String IGNORE_LIST_NAME = "ignore_name";
    String IGNORE_LIST_NUMBER = "ignore_number";
    // For future Use
    String IGNORE_LIST_CUSTOM = "ignore_custom_col01";

    String[] IGNORE_LIST_COLUMNS = new String[]{
            IGNORE_LIST_ID,
            IGNORE_LIST_NAME,
            IGNORE_LIST_NUMBER
    };

    String IGNORE_LIST_SQL = "CREATE TABLE " + IGNORE_LIST_TABLE + " (" +
            IGNORE_LIST_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            IGNORE_LIST_NAME + " TEXT NOT NULL, " +
            IGNORE_LIST_NUMBER + " TEXT NOT NULL, " +
            IGNORE_LIST_CUSTOM + " TEXT" +
            ");";

    // Create Tables
    String[] CREATE_TABLES_SQL = new String[]{
            MESSAGE_COUNTER_SQL
            ,IGNORE_LIST_SQL
    };
}
