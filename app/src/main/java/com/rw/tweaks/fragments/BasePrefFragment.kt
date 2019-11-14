package com.rw.tweaks.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import com.rw.tweaks.R
import com.rw.tweaks.dialogs.OptionDialog
import com.rw.tweaks.dialogs.SeekBarOptionDialog
import com.rw.tweaks.dialogs.SwitchOptionDialog
import com.rw.tweaks.prefs.SecureSeekBarPreference
import com.rw.tweaks.prefs.SecureSwitchPreference
import com.rw.tweaks.prefs.specific.*
import com.rw.tweaks.util.ISecurePreference

abstract class BasePrefFragment : PreferenceFragmentCompat() {
    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment = when (preference) {
            is SecureSwitchPreference -> SwitchOptionDialog.newInstance(preference.key, preference.disabled, preference.enabled)
            is SecureSeekBarPreference -> SeekBarOptionDialog.newInstance(preference.key, preference.minValue, preference.maxValue, preference.defaultValue, preference.units, preference.scale)
            is AnimationScalesPreference -> OptionDialog.newInstance(preference.key, R.layout.animation_dialog)
            is KeepDeviceOnPluggedPreference -> OptionDialog.newInstance(preference.key, R.layout.keep_device_plugged_dialog)
            is StorageThresholdPreference -> OptionDialog.newInstance(preference.key, R.layout.storage_thresholds)
            is CameraGesturesPreference -> OptionDialog.newInstance(preference.key, R.layout.camera_gestures)
            is AirplaneModeRadiosPreference -> OptionDialog.newInstance(preference.key, R.layout.airplane_mode_radios)
            else -> null
        }

        fragment?.setTargetFragment(this, 0)
        fragment?.show(fragmentManager!!, null)

        if (fragment == null) {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onBindPreferences() {
        markDangerous(preferenceScreen)
        super.onBindPreferences()
    }

    private fun markDangerous(group: PreferenceGroup) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is ISecurePreference && child.dangerous) {
                child.title = SpannableString(child.title).apply {
                    setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
                }
            }
            if (child is PreferenceGroup) markDangerous(child)
        }
    }
}