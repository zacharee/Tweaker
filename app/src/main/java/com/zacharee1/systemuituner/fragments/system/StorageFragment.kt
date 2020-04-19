package com.zacharee1.systemuituner.fragments.system

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.util.updateTitle

class StorageFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_storage, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.sub_storage)
    }
}