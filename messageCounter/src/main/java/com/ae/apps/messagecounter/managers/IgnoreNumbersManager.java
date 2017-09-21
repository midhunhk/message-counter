package com.ae.apps.messagecounter.managers;

import android.content.Context;

import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;

/**
 * Manages the access to Ignore List table and logic for excluding Ignored numbers from
 * being counted by the message counter
 */
public class IgnoreNumbersManager {

    private final Context mContext;

    /**
     * Creates an instance of the IgnoreNumbersManager
     * @param context context
     */
    public IgnoreNumbersManager(Context context){
        mContext = context;
    }

    public void testMethod(){
        CounterDataBaseAdapter counterDataBase = CounterDataBaseAdapter.getInstance(mContext);
    }

}
