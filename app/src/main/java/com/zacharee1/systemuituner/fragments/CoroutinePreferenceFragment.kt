package com.zacharee1.systemuituner.fragments

import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class CoroutinePreferenceFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {
    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}