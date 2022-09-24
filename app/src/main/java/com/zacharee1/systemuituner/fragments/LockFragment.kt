package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.updateTitle

class LockFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_lock, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.category_lock_screen)
    }
}