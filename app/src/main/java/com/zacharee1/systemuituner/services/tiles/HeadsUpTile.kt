package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeGlobal

@TargetApi(Build.VERSION_CODES.N)
class HeadsUpTile : TileService() {
    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == Settings.Global.getUriFor(Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED)) {
                updateState()
            }
        }
    }

    private val isEnabled: Boolean
        get() = getSetting(
            SettingsType.GLOBAL,
            Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
            0
        ) == "1"

    override fun onStartListening() {
        contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer)

        updateState()
    }

    override fun onStopListening() {
        try {
            contentResolver.unregisterContentObserver(observer)
        } catch (e: Exception) {}
    }

    private fun updateState() {
        qsTile?.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        setIcon()
        qsTile?.updateTile()
    }

    private fun setIcon() {
        qsTile?.icon = Icon.createWithResource(
            this,
            if (isEnabled) R.drawable.ic_baseline_notifications_active_24 else R.drawable.ic_baseline_notifications_off_24
        )
    }

    override fun onClick() {
        prefManager.saveOption(
            SettingsType.GLOBAL,
            Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
            if (isEnabled) 0 else 1
        )
        writeGlobal(Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED, if (isEnabled) 0 else 1)
        updateState()

        super.onClick()
    }
}