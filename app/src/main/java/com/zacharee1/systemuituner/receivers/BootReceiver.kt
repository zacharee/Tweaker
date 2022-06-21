package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zacharee1.systemuituner.App
import com.zacharee1.systemuituner.services.StartManagerWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            StartManagerWorker.start(context)
        }
    }
}