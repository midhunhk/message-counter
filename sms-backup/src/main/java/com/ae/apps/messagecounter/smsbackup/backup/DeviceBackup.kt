package com.ae.apps.messagecounter.smsbackup.backup

import android.content.Context
import com.ae.apps.messagecounter.smsbackup.models.Message
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class DeviceBackup : BackupMethod {

    companion object{
        const val FILE_NAME = "messagesBackup.backup"
    }

    override fun performBackup(context: Context, messages: List<Message>) {
        // Run IO on a background thread
        Thread(Runnable {
            val fos:FileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)

            // val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(fos)
            oos.writeObject(messages)
            oos.flush()
        })
    }

}