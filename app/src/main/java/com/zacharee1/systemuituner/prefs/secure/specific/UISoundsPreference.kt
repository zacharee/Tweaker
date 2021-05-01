package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.SettingsType

class UISoundsPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.CAR_DOCK_SOUND,
            Settings.Global.CAR_UNDOCK_SOUND,
            Settings.Global.DESK_DOCK_SOUND,
            Settings.Global.DESK_UNDOCK_SOUND,
            Settings.Global.LOCK_SOUND,
            Settings.Global.UNLOCK_SOUND,
            Settings.Global.LOW_BATTERY_SOUND,
            Settings.Global.TRUSTED_SOUND,
            Settings.Global.CHARGING_STARTED_SOUND,
            Settings.Global.CHARGING_SOUNDS_ENABLED
        ),
        SettingsType.SECURE to arrayOf(
            Settings.Secure.CHARGING_SOUNDS_ENABLED
        )
    )

    init {
        key = "ui_sounds"

        setTitle(R.string.feature_custom_ui_sounds)
        setSummary(R.string.feature_custom_ui_sounds_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_phonelink_ring_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_6)
    }
}