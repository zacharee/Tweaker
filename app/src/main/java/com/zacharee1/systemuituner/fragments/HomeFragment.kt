package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.updateTitle

class HomeFragment : BasePrefFragment() {
    override val supportsGrid = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_home, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.home)
    }
}