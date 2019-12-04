package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.base.BaseSecurePreference
import com.rw.tweaks.util.ISpecificPreference
import com.rw.tweaks.util.SettingsType

class StorageThresholdPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(
        Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES,
        Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE
    )

    init {
        key = "storage"

        setTitle(R.string.feature_insufficient_storage_warning)
        setSummary(R.string.feature_insufficient_storage_warning_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_disc_full_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)
    }
}