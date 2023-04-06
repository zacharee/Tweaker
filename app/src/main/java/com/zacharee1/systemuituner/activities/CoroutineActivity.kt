package com.zacharee1.systemuituner.activities

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class CoroutineActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}