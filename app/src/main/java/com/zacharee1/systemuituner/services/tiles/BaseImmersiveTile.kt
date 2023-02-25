package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import com.zacharee1.systemuituner.util.ImmersiveManager
import com.zacharee1.systemuituner.util.safeUpdateTile
import kotlinx.coroutines.launch

@TargetApi(Build.VERSION_CODES.N)
abstract class BaseImmersiveTile : CoroutineTileService() {
    protected abstract val type: ImmersiveManager.ImmersiveMode

    private val immersiveManager by lazy { ImmersiveManager(this) }
    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            updateState()
        }
    }

    override fun onStartListening() {
        contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.POLICY_CONTROL), true, observer)
        updateState()
    }

    override fun onStopListening() {
        contentResolver.unregisterContentObserver(observer)
    }

    override fun onClick() {
        val info = immersiveManager.parseAdvancedImmersive()
        val isOn = isOn(info)

        immersiveManager.loadInSavedLists(info)

        when (type) {
            ImmersiveManager.ImmersiveMode.FULL -> {
                if (isOn) {
                    info.allFull = false
                    info.fullApps.clear()
                    info.fullBl.clear()
                } else {
                    info.allFull = info.fullApps.isEmpty()
                }
            }
            ImmersiveManager.ImmersiveMode.NAV -> {
                if (isOn) {
                    info.allNav = false
                    info.navApps.clear()
                    info.navBl.clear()
                } else {
                    info.allNav = info.navApps.isEmpty()
                }
            }
            ImmersiveManager.ImmersiveMode.STATUS -> {
                if (isOn) {
                    info.allStatus = false
                    info.statusApps.clear()
                    info.statusBl.clear()
                } else {
                    info.allStatus = info.statusApps.isEmpty()
                }
            }
            ImmersiveManager.ImmersiveMode.NONE -> {}
        }

        launch {
            immersiveManager.setAdvancedImmersive(info)
            updateState()
        }
    }

    private fun updateState() {
        val info = immersiveManager.parseAdvancedImmersive()

        qsTile?.state = if (isOn(info)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.safeUpdateTile()
    }

    private fun isOn(info: ImmersiveManager.ImmersiveInfo): Boolean {
        return when (type) {
            ImmersiveManager.ImmersiveMode.FULL -> info.allFull || info.fullApps.isNotEmpty() || info.fullBl.isNotEmpty()
            ImmersiveManager.ImmersiveMode.NAV -> info.allNav || info.navApps.isNotEmpty() || info.navBl.isNotEmpty()
            ImmersiveManager.ImmersiveMode.STATUS -> info.allStatus || info.statusApps.isNotEmpty() || info.statusBl.isNotEmpty()
            else -> throw IllegalArgumentException("$type is not valid")
        }
    }
}