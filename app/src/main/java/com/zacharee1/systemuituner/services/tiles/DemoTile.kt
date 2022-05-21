package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.util.DemoController
import com.zacharee1.systemuituner.util.safeUpdateTile

@TargetApi(Build.VERSION_CODES.N)
class DemoTile : TileService(), (Boolean) -> Unit {
    private val demoHandler by lazy { DemoController.getInstance(this) }

    override fun onStartListening() {
        demoHandler.updateListeners.add(this)

        updateState()
    }

    override fun onStopListening() {
        demoHandler.updateListeners.remove(this)
    }

    override fun invoke(enabled: Boolean) {
        updateState()
    }

    private fun updateState() {
        qsTile?.state = if (demoHandler.isCurrentlyEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.safeUpdateTile()
    }

    override fun onClick() {
        if (demoHandler.isCurrentlyEnabled) {
            demoHandler.exitDemo()
        } else {
            demoHandler.enterDemo()
        }

        super.onClick()
    }
}