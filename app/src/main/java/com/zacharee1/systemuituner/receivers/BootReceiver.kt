package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.zacharee1.systemuituner.App
import com.zacharee1.systemuituner.services.StartManagerWorker
import com.zacharee1.systemuituner.util.BugsnagUtils

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            BugsnagUtils.leaveBreadcrumb("Boot action received.")
            if (Settings.canDrawOverlays(context)) {
                BugsnagUtils.leaveBreadcrumb("Can draw overlays, use StartManagerWorker.")
                StartManagerWorker.start(context)
            } else {
                BugsnagUtils.leaveBreadcrumb("Can't draw overlays, try starting Manager directly.")
                App.updateServiceState(context)
            }
        }
    }
}