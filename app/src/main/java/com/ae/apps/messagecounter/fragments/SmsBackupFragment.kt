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

class SmsBackupFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = SmsBackupFragment()
        const val DOG_NAME = "Cassiopedia"
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
            Thread(Runnable {
                run {
                    val messages = copyMessages()

                    // Create a list of backupMethods
                    val backupMethods = listOf<BackupMethod>(DeviceBackup())
                    backupMethods.forEach { it.performBackup(context!!, messages) }
                }
            }).start()
        }

        btnRestore.setOnClickListener {
            Toast.makeText(context, "Restore Button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyMessages(): List<Message> {
        val cursor = context?.contentResolver?.query(
                Uri.parse(SMSManager.SMS_URI_INBOX),
                SMS_TABLE_ALL_PROJECTION,
                null, null, null)
        val messages = mutableListOf<Message>()
        if (null != cursor && cursor.count > 0 && cursor.moveToFirst()) {
            val key = generateKey(DOG_NAME)
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
            } while( cursor.moveToNext() )
        }
        cursor?.close()

        activity!!.runOnUiThread( Runnable {
            Toast.makeText(activity, "Saving ${messages.size} messages", Toast.LENGTH_SHORT).show()
        })
        return messages
    }

}
