package com.rw.tweaks.fragments

import android.os.Bundle
import com.rw.tweaks.R
import com.rw.tweaks.util.updateTitle

class UIFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_ui, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.category_ui)
    }
}