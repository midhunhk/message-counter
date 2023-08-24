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
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.business.SMS_TABLE_ALL_PROJECTION
import com.ae.apps.messagecounter.data.business.getMessageForBackupFromCursor
import com.ae.apps.messagecounter.data.models.Message
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.smsbackup.BackupMethod
import com.ae.apps.messagecounter.data.smsbackup.DeviceBackup
import com.ae.apps.messagecounter.data.smsbackup.encryptMsg
import com.ae.apps.messagecounter.data.smsbackup.generateKey
import com.ae.apps.messagecounter.databinding.FragmentSmsBackupBinding
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*

class SmsBackupFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = SmsBackupFragment()
        const val DATE_FORMAT = "dd-MMM-yyyy hh:mm a"
    }

    private var isBackupRunning = false
    lateinit var preferences: PreferenceRepository
    private lateinit var binding:FragmentSmsBackupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSmsBackupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(requireContext()))

        initUI()
    }

    private fun initUI() {
        // FIXME
        binding.btnBackup.setOnClickListener {
            if (!isBackupRunning) {
                updateUIForStartBackup()

                doAsync {
                    var errorMessage: String? = null
                    val messages = readAllMessages()

                    try {
                        // Create a list of backupMethods
                        val backupMethods = listOf<BackupMethod>(DeviceBackup())
                        backupMethods.forEach { it.performBackup(requireContext(), messages) }
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }

                    /*
                    withContext(Main) {
                        updateUIForEndBackup()
                        if (errorMessage == null) {
                            preferences.setLastBackupTime( Calendar.getInstance().timeInMillis )
                            updateBackupStatus()
                            context?.longToast("Saved ${messages.size} messages")
                        } else {
                            context?.longToast("Error Occurred: $errorMessage")
                        }
                    }
                     */
                }
            }
        }

        // Hide the progressbar on load
        binding.progressBarBackup.visibility = View.GONE

        updateBackupStatus()
    }

    private fun updateBackupStatus() {
        val storedBackupTime = preferences.getLastBackupTime()
        if (storedBackupTime == 0L) {
            val textNone = getString(R.string.str_sms_backup_status_none)
            //textBackupStatus.text = getString(R.string.str_sms_backup_status, textNone)
        } else {
            val lastBackupDate = Calendar.getInstance()
            lastBackupDate.timeInMillis = storedBackupTime
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            //textBackupStatus.text = getString(R.string.str_sms_backup_status, dateFormat.format(lastBackupDate.time))
        }
    }

    private fun updateUIForEndBackup() {
        isBackupRunning = false
        //btnBackup.isEnabled = true
        //progressBarBackup.visibility = View.GONE
    }

    private fun updateUIForStartBackup() {
        isBackupRunning = true
//        progressBarBackup.visibility = View.VISIBLE
 //       btnBackup.isEnabled = false
    }

    private fun readAllMessages(): List<Message> {
        val messages = mutableListOf<Message>()
        // TODO
        /*
        val cursor = activity!!.contentResolver.query(
                Uri.parse(SMSManager.SMS_URI_INBOX),
                SMS_TABLE_ALL_PROJECTION,
                null, null, null)

        cursor.use {
            if (null != cursor && cursor.count > 0 && cursor.moveToFirst()) {
                val backupName = getString(R.string.str_sms_backup_method_name)
                val key = generateKey(backupName)
                do {
                    val message = getMessageForBackupFromCursor(cursor)
                    val encryptedMessage = message.copy(
                            address = encryptMsg(message.address, key).toString(),
                            body = encryptMsg(message.body, key).toString()
                    )
                    messages.add(encryptedMessage)
                } while (cursor.moveToNext())
            }
        }

         */

        return messages
    }

}
