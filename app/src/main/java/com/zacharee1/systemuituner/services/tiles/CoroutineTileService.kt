package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@TargetApi(Build.VERSION_CODES.N)
abstract class CoroutineTileService : TileService(), CoroutineScope by MainScope() {
    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}