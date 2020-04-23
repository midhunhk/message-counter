package com.ae.apps.messagecounter.smsbackup.backup

import android.content.Context
import com.ae.apps.messagecounter.smsbackup.models.Message
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class DeviceBackup : BackupMethod {

    companion object {
        const val FILE_NAME = "messagesBackup.backup"
    }

    override fun performBackup(context: Context, messages: List<Message>) {
        // Run IO on a background thread
        Thread(Runnable {
            run {
                // Delete any existing backups
                context.deleteFile(FILE_NAME)

                val fileOutputStream: FileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                val objectOutputStream = ObjectOutputStream(fileOutputStream)
                objectOutputStream.writeObject(messages)
                objectOutputStream.flush()
            }
        }).start()
    }

}