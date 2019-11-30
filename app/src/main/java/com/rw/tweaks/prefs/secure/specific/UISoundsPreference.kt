package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.ISpecificPreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.SettingsType

class UISoundsPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(
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
    )

    init {
        key = "ui_sounds"

        setTitle(R.string.feature_custom_ui_sounds)
        setSummary(R.string.feature_custom_ui_sounds_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_phonelink_ring_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_6)

        init(this)
    }
}