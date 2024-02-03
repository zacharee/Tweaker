package com.zacharee1.systemuituner.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.zacharee1.systemuituner.App
import com.zacharee1.systemuituner.util.BugsnagUtils

class StartManagerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        fun start(context: Context) {
            val request = OneTimeWorkRequest.from(StartManagerWorker::class.java)
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        BugsnagUtils.leaveBreadcrumb("Attempting to start Manager service through StartManagerWorker.")
        App.updateServiceState(applicationContext)
        return Result.success()
    }
}