package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.updateTitle

class NotificationsFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_notifications, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.category_notifications)
    }
}