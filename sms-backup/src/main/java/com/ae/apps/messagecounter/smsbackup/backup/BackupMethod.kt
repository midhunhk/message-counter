package com.ae.apps.messagecounter.smsbackup.backup

import android.content.Context
import com.ae.apps.messagecounter.smsbackup.models.Message

interface BackupMethod {

    fun performBackup(context: Context, messages: List<Message>)

}