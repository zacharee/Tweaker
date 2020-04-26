package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.content.*
import android.os.Build
import android.os.Handler
import android.provider.AlarmClock
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.text.format.DateFormat
import kotlinx.coroutines.*
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
            updateTime()
        }
    }
    private val handler = Handler()

    private var shouldRun = false

    override fun onStartListening() {
        shouldRun = true

        registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
        updateTime()

        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()
    }

    override fun onStopListening() {
        shouldRun = false

        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {}
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

        try {
            startActivityAndCollapse(intentClock)
        } catch (e: Exception) {}
    }

    private fun updateTime() {
        val formatOption = if (DateFormat.is24HourFormat(this@ClockTile)) FORMAT_24 else FORMAT_12
        val date = SimpleDateFormat(formatOption, Locale.getDefault()).format(Date())

        qsTile?.label = date
        qsTile?.updateTile()

        if (shouldRun) {
            handler.postDelayed({
                updateTime()
            }, 1000)
        }
    }
}