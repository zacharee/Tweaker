package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import com.zacharee1.systemuituner.R

@SuppressLint("RestrictedApi")
class ManageQSFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_manage_qs, rootKey)
    }
}