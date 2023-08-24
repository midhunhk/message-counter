/*
 *  Copyright 2020 Midhun Harikumar
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ae.apps.messagecounter.data.smsbackup

import android.content.Context
import com.ae.apps.messagecounter.data.models.Message
import org.jetbrains.anko.doAsync
import java.io.FileOutputStream
import java.io.ObjectOutputStream

/**
 * An implementation of @{BackupMethod} that represents backing up to local disk
 */
class DeviceBackup : BackupMethod {

    companion object {
        const val FILE_NAME = "messagesBackup.backup"
    }

    override fun performBackup(context: Context, messages: List<Message>) {
        // Run IO on a background thread
        doAsync {
            // Delete any existing backups
            context.deleteFile(FILE_NAME)

            val fileOutputStream: FileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(messages)
            objectOutputStream.flush()
        }
    }
}
