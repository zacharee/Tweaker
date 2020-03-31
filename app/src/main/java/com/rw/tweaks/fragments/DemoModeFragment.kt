package com.rw.tweaks.fragments

import android.os.Bundle
import com.rw.tweaks.util.DemoController

class DemoModeFragment : BasePrefFragment() {
    init {
        preferenceManager.sharedPreferencesName = DemoController.DEMO_PREFS
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }
}