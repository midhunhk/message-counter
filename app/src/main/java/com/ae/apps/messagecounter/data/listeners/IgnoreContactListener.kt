/*
 * Copyright 2018 Midhun Harikumar
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