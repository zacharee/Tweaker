package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.zacharee1.systemuituner.App
import com.zacharee1.systemuituner.services.StartManagerWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            if (Settings.canDrawOverlays(context)) {
                StartManagerWorker.start(context)
            } else {
                App.updateServiceState(context)
            }
        }
    }
}