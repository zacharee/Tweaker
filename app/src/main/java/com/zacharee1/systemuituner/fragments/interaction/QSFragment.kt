package com.zacharee1.systemuituner.fragments.interaction

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.util.updateTitle

class QSFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_qs, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.category_quick_settings)
    }
}