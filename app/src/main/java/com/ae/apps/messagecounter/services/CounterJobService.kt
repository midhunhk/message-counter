package com.ae.apps.messagecounter.services

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
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.observers.SMSObserver
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast

@RequiresApi(Build.VERSION_CODES.N)
class CounterJobService : JobService() {

    companion object {
        private const val THREAD_NAME = "AnotherThread"
        private const val JOB_ID = 180803
        private const val DELAY_MIN: Long = 500
        private const val DELAY_MAX: Long = 1000 * 5

        fun registerJob(context: Context): Boolean {
            val component = ComponentName(context, CounterJobService::class.java)
            val contentUri = JobInfo.TriggerContentUri(Uri.parse(SMSManager.SMS_URI_ALL),
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS)
            val jobInfo = JobInfo.Builder(JOB_ID, component)
                    .addTriggerContentUri(contentUri)
                    .setTriggerContentUpdateDelay(DELAY_MIN)
                    .setTriggerContentMaxDelay(DELAY_MAX)
                    .build()

            // Schedule a Job if not already done so
            val scheduler: JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            if (CounterServiceHelper.isJobNotRunning(scheduler, JOB_ID)) {
                val result = scheduler.schedule(jobInfo)
                return (result == JobScheduler.RESULT_SUCCESS)
            }

            return false
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val context = baseContext
        val handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        longToast("onStartJob")

        doAsync {
            val observer = SMSObserver(handler, context)
            observer.onChange(false)

            jobFinished(params, true)
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

}