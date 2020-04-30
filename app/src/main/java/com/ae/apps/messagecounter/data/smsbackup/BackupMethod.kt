package com.ae.apps.messagecounter.data.smsbackup

import android.content.Context
import com.ae.apps.messagecounter.data.models.Message

interface BackupMethod {

    fun performBackup(context: Context, messages: List<Message>)

}