package com.ae.apps.messagecounter.managers;

import android.content.Context;
import android.database.Cursor;

import com.ae.apps.messagecounter.db.CounterDataBaseAdapter;
import com.ae.apps.messagecounter.db.CounterDataBaseConstants;
import com.ae.apps.messagecounter.models.IgnoredContact;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Returns all ignored numbers from the database
     *
     * @return List of IgnoredContacts
     */
    public List<IgnoredContact> allIgnoredNumbers(){
        CounterDataBaseAdapter counterDataBase = CounterDataBaseAdapter.getInstance(mContext);
        List<IgnoredContact> ignoredContacts = new ArrayList<>();

        Cursor cursor = counterDataBase.allIgnoredContacts();
        if(null != cursor && cursor.moveToFirst()){
            IgnoredContact ignoredContact;
            do{
                ignoredContact = new IgnoredContact();
                ignoredContact.setId(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_ID)));
                ignoredContact.setName(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_NAME)));
                ignoredContact.setNumber(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_NUMBER)));
                ignoredContacts.add(ignoredContact);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return ignoredContacts;
    }

    /**
     * Add an IgnoredContact to the database
     *
     * @param ignoredContact new contact
     * @return Updated IgnoredContact with id
     */
    public IgnoredContact addIgnoredContact(IgnoredContact ignoredContact){
        CounterDataBaseAdapter counterDataBase = CounterDataBaseAdapter.getInstance(mContext);
        long id = counterDataBase.addNumberToIgnore(ignoredContact.getName(), ignoredContact.getNumber());
        ignoredContact.setId(String.valueOf(id));
        return ignoredContact;
    }

    /**
     * Remove an IgnoredContact from the ignored table
     *
     * @param ignoredContact contact to be un ignored
     */
    public void removeIgnoredContact(IgnoredContact ignoredContact){
        CounterDataBaseAdapter counterDataBase = CounterDataBaseAdapter.getInstance(mContext);
        counterDataBase.removeNumberFromIgnore(Long.valueOf(ignoredContact.getId()));
    }

    /**
     * Checks if a number is already ignored
     *
     * @param ignoredContact IgnoredContact to check
     * @return if number is already ignored or not
     */
    public boolean checkIfNumberIgnored(IgnoredContact ignoredContact){
        CounterDataBaseAdapter counterDataBase = CounterDataBaseAdapter.getInstance(mContext);
        return counterDataBase.checkIgnoredNumber(ignoredContact.getNumber());
    }

}
