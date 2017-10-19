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

    private static IgnoreNumbersManager sInstance;
    private final CounterDataBaseAdapter counterDataBase;
    private transient List<String> mIgnoredNumbersCache;

    /**
     * Creates an instance of the IgnoreNumbersManager
     *
     * @param context context
     */
    private IgnoreNumbersManager(Context context) {
        counterDataBase = CounterDataBaseAdapter.getInstance(context);
    }

    public static IgnoreNumbersManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IgnoreNumbersManager(context);
        }
        return sInstance;
    }

    /**
     * Returns all ignored numbers as IgnoredContact model
     *
     * @return List of IgnoredContacts
     */
    public List<IgnoredContact> allIgnoredContacts() {
        List<IgnoredContact> ignoredContacts = new ArrayList<>();

        Cursor cursor = counterDataBase.allIgnoredContacts();
        if (null != cursor && cursor.moveToFirst()) {
            IgnoredContact ignoredContact;
            do {
                ignoredContact = new IgnoredContact();
                ignoredContact.setId(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_ID)));
                ignoredContact.setName(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_NAME)));
                ignoredContact.setNumber(cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_NUMBER)));
                ignoredContacts.add(ignoredContact);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ignoredContacts;
    }

    /**
     * Returns List of Ignored Numbers as a Collection of numbers
     * Reads the data from the database initially and caches into a local
     * List and serves from it until the cache is invalidated
     *
     * @return List of Ignored Numbers
     * @see {IgnoreNumbersManager.invalidateIgnoredNumbersCache()}
     */
    protected List<String> getIgnoredNumbers() {
        if (null == mIgnoredNumbersCache || mIgnoredNumbersCache.isEmpty()) {
            mIgnoredNumbersCache = new ArrayList<>();
            String number;
            Cursor cursor = counterDataBase.allIgnoredContacts();
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    number = cursor.getString(cursor.getColumnIndex(CounterDataBaseConstants.IGNORE_LIST_NUMBER));
                    mIgnoredNumbersCache.add(number);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return mIgnoredNumbersCache;
    }

    /**
     * Invalidates the cached Ignored Numbers. Should be called when the database
     * entries are added or removed
     */
    protected void invalidateIgnoredNumbersCache() {
        mIgnoredNumbersCache = null;
    }

    /**
     * Add an IgnoredContact to the database
     *
     * @param ignoredContact new contact
     * @return Updated IgnoredContact with id
     */
    public IgnoredContact addIgnoredContact(IgnoredContact ignoredContact) {
        long id = counterDataBase.addNumberToIgnore(ignoredContact.getName(), ignoredContact.getNumber());
        ignoredContact.setId(String.valueOf(id));
        invalidateIgnoredNumbersCache();
        return ignoredContact;
    }

    /**
     * Remove an IgnoredContact from the ignored table
     *
     * @param ignoredContact contact to be un ignored
     */
    public void removeIgnoredContact(IgnoredContact ignoredContact) {
        counterDataBase.removeNumberFromIgnore(Long.valueOf(ignoredContact.getId()));
        invalidateIgnoredNumbersCache();
    }

    /**
     * Checks if a number is already ignored
     *
     * @param number number to check if its already ignored
     * @return if number is already ignored or not
     */
    public boolean checkIfNumberIgnored(String number) {
        // If number is not present, we can't check if it present in the ignored list
        if (null == number) {
            return false;
        }
        // Checking against a local cache instead of hitting the database
        // as there shouldn't be a lot of IgnoredNumbers in normal use cases
        return getIgnoredNumbers().contains(number.replaceAll("\\+", ""));
    }

}
