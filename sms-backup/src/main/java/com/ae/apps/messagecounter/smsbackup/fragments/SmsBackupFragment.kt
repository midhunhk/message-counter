package com.ae.apps.messagecounter.smsbackup.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.smsbackup.backup.BackupMethod
import com.ae.apps.messagecounter.smsbackup.backup.DeviceBackup
import com.ae.apps.messagecounter.smsbackup.models.Message

class SmsBackupFragment : Fragment() {

    var COLUMN_NAME_PROTOCOL = "protocol"
    var COLUMN_NAME_ID = "_id"
    var COLUMN_NAME_DATE = "date"
    var COLUMN_NAME_PERSON = "person"
    var COLUMN_NAME_BODY = "body"
    var COLUMN_NAME_ADDRESS = "address"

    var SMS_TABLE_PROJECTION = arrayOf(
            COLUMN_NAME_ID,
            COLUMN_NAME_DATE,
            COLUMN_NAME_PERSON,
            COLUMN_NAME_BODY,
            COLUMN_NAME_PROTOCOL,
            COLUMN_NAME_ADDRESS)

    companion object {
        @JvmStatic
        fun newInstance() = SmsBackupFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val messages = copyMessages()

        // Create a list of backupMethods
        val backupMethods = listOf<BackupMethod>(DeviceBackup())
        backupMethods.forEach{ it.performBackup(context!!, messages) }
    }

    private fun copyMessages(): List<Message> {
        val cursor = context?.contentResolver?.query(
                Uri.parse(SMSManager.SMS_URI_INBOX),
                SMS_TABLE_PROJECTION,
                null, null, null)
        val messages = mutableListOf<Message>()
        if (null != cursor && cursor.count > 0 && cursor.moveToFirst()) {
            val messageId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID))
            val address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS))
            val person = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PERSON))
            val protocol = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROTOCOL))
            val sentTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DATE))
            val body = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BODY))

            messages.add( Message(messageId, person, body, sentTime,protocol,address) )
        }
        cursor?.close()

        return messages
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sms_backup, container, false)
    }
}
