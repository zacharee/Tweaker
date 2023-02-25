package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.launch

@TargetApi(Build.VERSION_CODES.N)
class NightModeTile : CoroutineTileService() {
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
        launch {
            if (isActive) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    writeSetting(SettingsType.SECURE, "twilight_mode", 0, saveOption = true)
                } else {
                    writeSetting(SettingsType.SECURE, "night_display_activated", 0, saveOption = true)
                }
            } else {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    writeSetting(SettingsType.SECURE, "twilight_mode", 1, saveOption = true)
                } else {
                    writeSetting(SettingsType.SECURE, "night_display_activated", 1, saveOption = true)
                }
            }

            updateState()
        }
    }

    private fun updateState() {
        qsTile?.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.safeUpdateTile()
    }
}