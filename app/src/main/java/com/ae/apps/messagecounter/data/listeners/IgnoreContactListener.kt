package com.ae.apps.messagecounter.data.listeners

import com.ae.apps.messagecounter.data.models.IgnoredNumber

/**
 * Activity or Fragment that wants to get notified when operations
 * on an IgnoreContact are done
 */
interface IgnoreContactListener {
    /**
     * Invoked when a contact has been selected for blocking
     *
     * @param ignoredContact ignored contact
     */
    fun onContactSelected(ignoredContact: IgnoredNumber)

    /**
     *
     * @param ignoredContact
     */
    fun onIgnoredContactRemoved(ignoredContact: IgnoredNumber)
}