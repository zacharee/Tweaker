package com.zacharee1.systemuituner.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.zacharee1.systemuituner.App

class StartManagerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        fun start(context: Context) {
            val request = OneTimeWorkRequest.from(StartManagerWorker::class.java)
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        App.updateServiceState(applicationContext)
        return Result.success()
    }
}