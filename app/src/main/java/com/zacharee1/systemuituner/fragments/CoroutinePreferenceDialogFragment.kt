package com.zacharee1.systemuituner.fragments

import androidx.preference.PreferenceDialogFragmentCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class CoroutinePreferenceDialogFragment : PreferenceDialogFragmentCompat(), CoroutineScope by MainScope() {
    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}