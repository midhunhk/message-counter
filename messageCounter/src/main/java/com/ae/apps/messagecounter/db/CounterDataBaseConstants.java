package com.ae.apps.messagecounter.db;

/**
 * Constants for CounterDatabase
 */
interface CounterDataBaseConstants {
    int DATABASE_VERSION = 1;

    String DATABASE_NAME = "db_message_counter";
    String TABLE_COUNTER = "tbl_sms_counter";

    /* Table keys */
    String KEY_DATE = "date_index";
    String KEY_COUNT = "sent_count";

    /* The SQL to create the table */
    String COUNTER_TABLE_CREATE = "CREATE TABLE " + TABLE_COUNTER + " ("
            + KEY_DATE + " NUMERIC PRIMARY KEY, "
            + KEY_COUNT + " NUMERIC NOT NULL);";

    /* Projection for retrieving the data */
    String[] DATA_PROJECTION = new String[]{KEY_DATE, KEY_COUNT};

    String[] CREATE_TABLES_SQL = new String[]{COUNTER_TABLE_CREATE};
}
