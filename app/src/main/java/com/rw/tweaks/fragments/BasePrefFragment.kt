package com.rw.tweaks.fragments

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.dialogs.OptionDialog
import com.rw.tweaks.dialogs.SeekBarOptionDialog
import com.rw.tweaks.dialogs.SwitchOptionDialog
import com.rw.tweaks.prefs.*

abstract class BasePrefFragment : PreferenceFragmentCompat() {
    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment = when (preference) {
            is SecureSwitchPreference -> SwitchOptionDialog.newInstance(preference.key, preference.disabled, preference.enabled)
            is SecureSeekBarPreference -> SeekBarOptionDialog.newInstance(preference.key, preference.minValue, preference.maxValue, preference.defaultValue, preference.units, preference.scale)
            is AnimationScalesPreference -> OptionDialog.newInstance(preference.key, R.layout.animation_dialog)
            is KeepDeviceOnPluggedPreference -> OptionDialog.newInstance(preference.key, R.layout.keep_device_plugged_dialog)
            is StorageThresholdPreference -> OptionDialog.newInstance(preference.key, R.layout.storage_thresholds)
            else -> null
        }

        fragment?.setTargetFragment(this, 0)
        fragment?.show(fragmentManager!!, null)

        if (fragment == null) {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}