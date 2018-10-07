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
package com.ae.apps.messagecounter.services

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.BuildConfig
import com.ae.apps.messagecounter.observers.SMSObserver
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.N)
class CounterJobService : JobService() {

    companion object {
        private const val THREAD_NAME = "AnotherThread"
        private const val JOB_ID = 180803
        private const val DELAY_MIN: Long = 500
        private const val DELAY_MAX: Long = 1000 * 4

        fun registerJob(context: Context, cancelAndReschedule: Boolean = false): Boolean {
            val component = ComponentName(context, CounterJobService::class.java)
            val contentUri = JobInfo.TriggerContentUri(Uri.parse(SMSManager.SMS_URI_ALL),
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS)
            val jobInfo = JobInfo.Builder(JOB_ID, component)
                    .addTriggerContentUri(contentUri)
                    .setTriggerContentUpdateDelay(DELAY_MIN)
                    .setTriggerContentMaxDelay(DELAY_MAX)
                    .setMinimumLatency(DELAY_MIN)
                    .setBackoffCriteria(DELAY_MAX, JobInfo.BACKOFF_POLICY_LINEAR)
                    .build()

            val scheduler: JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val isJobRunning = CounterServiceHelper.isJobRunning(scheduler, JOB_ID)

            if (isJobRunning) {
                if (cancelAndReschedule) {
                    // Toast.makeText(context, "Cancelling current job", Toast.LENGTH_SHORT).show()
                    scheduler.cancel(JOB_ID)
                    scheduler.cancelAll()
                } else {
                    return true
                }
            }
            val result = scheduler.schedule(jobInfo)
            Toast.makeText(context, "Job scheduled ${result == 1}, total jobs $scheduler.allPendingJobs.size", Toast.LENGTH_SHORT).show()
            return (result == JobScheduler.RESULT_SUCCESS)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onStartJob(params: JobParameters?): Boolean {
        val context = baseContext
        val handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        // Debug toast with JobStart time
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timeNow = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
            longToast("onStartJob at $timeNow")
        }

        doAsync {
            val observer = SMSObserver(handler, context)
            observer.onChange(false)

            jobFinished(params, false)

            // Reschedule another job to monitor SMS Content Provider
            runOnUiThread {
                registerJob(context, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        longToast("onStopJob")
        return true
    }

}