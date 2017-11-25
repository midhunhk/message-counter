/*
 * Copyright 2013 Midhun Harikumar
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

package com.ae.apps.messagecounter.utils;

/**
 * The constants used in the application
 *
 * @author Midhun
 */
public interface AppConstants {

    /* Colorful set of colors that can be used for a chart */
    int[] CHART_COLORFUL = {0xFFB2C938, 0xff3BA9B8, 0xffFF9910, 0xffC74C47, 0xff5B1A69,
            0xffA83AAE, 0xffF870BD, 0xff7AD1C4, 0xff419DDB};

    /*
     * Colors taken from http://www.mulinblog.com/a-color-palette-optimized-for-data-visualization/
     */
    int[] CHART_COLORFUL2 = {0xFF5DA5DA, 0xff4D4D4D, 0xffFAA43A, 0xff60BD68, 0xffF17CB0,
            0xffB2912F, 0xffB276B2, 0xffDECF3F, 0xffF15854};

	/* 0xffFFD302, yellow */

    /* The delay before starting the service */
    long SERVICE_START_DELAY = 10 * 1000;

    int MAX_ROWS_IN_CHART = 8;
    int DEFAULT_MESSAGE_LIMIT = 100;
    int NOTIFICATION_REQUEST_CODE = 1042;
    long NAV_MENU_DONATE = 4001;
    long NAV_MENU_SETTINGS = 4002;
    long NAV_MENU_SHARE = 4003;
    long NAV_MENU_ABOUT = 4004;
    String DEFAULT_CYCLE_START_DATE = "1";

    /* Preference keys */
    String PREF_KEY_HIDE_NON_CONTACT_MESSAGES = "pref_key_hide_non_contact_messages";
    String PREF_KEY_CYCLE_START_DATE = "pref_key_cycle_start_date";
    String PREF_KEY_MESSAGE_LIMIT_VALUE = "pref_key_message_limit_value";
    String PREF_KEY_ENABLE_SENT_COUNT = "pref_key_enable_sent_message_count";
    String PREF_KEY_ENABLE_NOTIFICATION = "pref_key_enable_notification";
    String PREF_KEY_NAV_DRAWER_INTRO_GIVEN = "pref_key_nav_drawer_intro_given";
    String PREF_KEY_DONATIONS_MADE = "pref_key_donations_made";
    String PREF_KEY_LAST_SENT_MESSAGE_ID = "pref_key_last_sent_message_id";
    String PREF_KEY_LAST_SENT_TIME_STAMP = "pref_key_last_sent_message_time_stamp";

    String PREF_KEY_ENABLE_OFFLINE_COUNT = "pref_key_enable_offline_count";
    String PREF_KEY_HISTORIC_DATA_INDEXED = "pref_key_historic_data_indexed";

}
