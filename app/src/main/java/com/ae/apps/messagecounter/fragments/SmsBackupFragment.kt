/*
 * Copyright 2020 Midhun Harikumar
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
package com.ae.apps.messagecounter.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.SMS_TABLE_ALL_PROJECTION
import com.ae.apps.messagecounter.data.business.getMessageForBackupFromCursor
import com.ae.apps.messagecounter.data.models.Message
import com.ae.apps.messagecounter.data.smsbackup.BackupMethod
import com.ae.apps.messagecounter.data.smsbackup.DeviceBackup
import com.ae.apps.messagecounter.data.smsbackup.encryptMsg
import com.ae.apps.messagecounter.data.smsbackup.generateKey
import kotlinx.android.synthetic.main.fragment_sms_backup.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread

class SmsBackupFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = SmsBackupFragment()
        const val BACKUP_FRAGMENT_NAME = "Cassiopeia"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sms_backup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initUI()
    }

    private fun initUI() {
        btnBackup.setOnClickListener {
            doAsync {
                val messages = readAllMessages()

                // Create a list of backupMethods
                val backupMethods = listOf<BackupMethod>(DeviceBackup())
                backupMethods.forEach { it.performBackup(context!!, messages) }

                uiThread {
                    context?.longToast("Saved ${messages.size} messages")
                }
            }

        }

        btnRestore.setOnClickListener {
            Toast.makeText(context, "Restore Button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readAllMessages(): List<Message> {
        val messages = mutableListOf<Message>()
        val cursor = activity!!.contentResolver.query(
                Uri.parse(SMSManager.SMS_URI_INBOX),
                SMS_TABLE_ALL_PROJECTION,
                null, null, null)

        cursor.use {
            if (null != cursor && cursor.count > 0 && cursor.moveToFirst()) {
                val key = generateKey(BACKUP_FRAGMENT_NAME)
                do {
                    val message = getMessageForBackupFromCursor(cursor)
                    val encryptedMessage = Message(
                            message.id,
                            message.messageCount,
                            encryptMsg(message.body, key).toString(),
                            message.date,
                            message.protocol,
                            message.address,
                            message.person
                    )
                    messages.add(encryptedMessage)
                } while (cursor.moveToNext())
            }
        }

        return messages
    }

}
