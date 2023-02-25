package com.zacharee1.systemuituner.activities

import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class CoroutineActivity : ComponentActivity(), CoroutineScope by MainScope() {
    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}