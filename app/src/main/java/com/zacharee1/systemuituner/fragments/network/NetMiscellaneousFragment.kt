package com.zacharee1.systemuituner.fragments.network

import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.util.updateTitle

class NetMiscellaneousFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_net_misc, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.sub_miscellaneous)
    }
}