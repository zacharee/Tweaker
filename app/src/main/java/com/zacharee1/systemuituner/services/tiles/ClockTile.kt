package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import com.zacharee1.systemuituner.util.safeUpdateTile
import java.text.SimpleDateFormat
import java.util.*

@TargetApi(Build.VERSION_CODES.N)
class ClockTile : TileService() {
    companion object {
        const val FORMAT_12 = "h:mm:ss"
        const val FORMAT_24 = "HH:mm:ss"
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) {
                updateTime()
            }
        }
    }
    private val handler = Handler(Looper.getMainLooper())

    private var shouldRun = false

    override fun onStartListening() {
        shouldRun = true

        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(Intent.ACTION_TIME_TICK),
            ContextCompat.RECEIVER_EXPORTED,
        )
        updateTime()

        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.safeUpdateTile()
    }

    override fun onStopListening() {
        shouldRun = false

        try {
            unregisterReceiver(receiver)
        } catch (_: Exception) {}
    }

    override fun onClick() {
        openClock()

        super.onClick()
    }

    override fun onDestroy() {
        shouldRun = false
        handler.removeCallbacksAndMessages(null)
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun openClock() {
        val intentClock = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        intentClock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntentActivityWrapper(
            this, 100, intentClock,
            PendingIntent.FLAG_UPDATE_CURRENT, false,
        )

        try {
            TileServiceCompat.startActivityAndCollapse(this, pendingIntent)
        } catch (_: Exception) {}
    }

    private fun updateTime() {
        val formatOption = if (DateFormat.is24HourFormat(this@ClockTile)) FORMAT_24 else FORMAT_12
        val date = SimpleDateFormat(formatOption, Locale.getDefault()).format(Date())

        qsTile?.label = date
        qsTile?.safeUpdateTile()

        if (shouldRun) {
            handler.postDelayed({
                updateTime()
            }, 1000)
        }
    }
}