package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.updateTitle

class AdvancedFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_advanced, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.advanced)
    }
}