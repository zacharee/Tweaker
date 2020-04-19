package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.services.Manager
import com.zacharee1.systemuituner.util.prefManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (context.prefManager.persistentOptions.isNotEmpty()) {
                ContextCompat.startForegroundService(context, Intent(context, Manager::class.java))
            }
        }
    }
}