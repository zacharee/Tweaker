package com.rw.tweaks.fragments

import android.os.Bundle
import com.rw.tweaks.R
import com.rw.tweaks.util.updateTitle

class AppsFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_apps, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.category_apps)
    }
}