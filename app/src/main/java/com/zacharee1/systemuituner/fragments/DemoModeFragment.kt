package com.zacharee1.systemuituner.fragments

import android.content.SharedPreferences
import android.os.Bundle
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.DemoController

class DemoModeFragment : BasePrefFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val controller by lazy { DemoController.getInstance(requireContext()) }
    private val enabled: Boolean
        get() = preferenceManager.sharedPreferences!!.getString("demo_enabled", "false")!!.toBoolean()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = DemoController.DEMO_PREFS
        setPreferencesFromResource(R.xml.prefs_demo, rootKey)

        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            "demo_enabled" -> {
                if (enabled) {
                    controller.enterDemo()
                } else {
                    controller.exitDemo()
                }
            }
            else -> {
                if (enabled) {
                    controller.enterDemo()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }
}