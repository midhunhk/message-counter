package com.ae.apps.messagecounter.data;

import com.ae.apps.messagecounter.models.IgnoredContact;

/**
 * Activity or Fragment that wants to get notified when operations
 * on an IgnoreContact are done
 */
public interface IgnoreContactListener {
    /**
     * Invoked when a contact has been selected for blocking
     *
     * @param ignoredContact ignored contact
     */
    void onContactSelected(IgnoredContact ignoredContact);

    /**
     *
     * @param ignoredContact
     */
    void onIgnoredContactRemoved(IgnoredContact ignoredContact);
}
