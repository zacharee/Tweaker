package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.dialogs.DonateDialog
import com.zacharee1.systemuituner.util.updateTitle

class HomeFragment : BasePrefFragment() {
    override val supportsGrid = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_home, rootKey)

        findPreference<Preference>("donate")?.setOnPreferenceClickListener {
            DonateDialog(requireActivity())
                .show()
            true
        }
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.home)
    }
}