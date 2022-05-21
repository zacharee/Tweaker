package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.util.*

@TargetApi(Build.VERSION_CODES.N)
class NightModeTile : TileService() {
    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            updateState()
        }
    }

    private val isActive: Boolean
        get() = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            getSetting(SettingsType.SECURE, "twilight_mode", 0) != "0"
        else
            getSetting(SettingsType.SECURE, "night_display_activated", 0) == "1"

    override fun onStartListening() {
        val uri = Settings.Secure.getUriFor(if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            "night_display_activated"
        } else {
            "twilight_mode"
        })

        contentResolver.registerContentObserver(uri, true, observer)

        updateState()
    }

    override fun onStopListening() {
        contentResolver.unregisterContentObserver(observer)
    }

    override fun onClick() {
        if (isActive) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                prefManager.saveOption(SettingsType.SECURE, "twilight_mode", 0)
                writeSecure("twilight_mode", 0)
            } else {
                prefManager.saveOption(SettingsType.SECURE, "night_display_activated", 0)
                writeSecure("night_display_activated", 0)
            }
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                prefManager.saveOption(SettingsType.SECURE, "twilight_mode", 1)
                writeSecure("twilight_mode", 1)
            } else {
                prefManager.saveOption(SettingsType.SECURE, "night_display_activated", 1)
                writeSecure("night_display_activated", 1)
            }
        }

        updateState()
    }

    private fun updateState() {
        qsTile?.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.safeUpdateTile()
    }
}