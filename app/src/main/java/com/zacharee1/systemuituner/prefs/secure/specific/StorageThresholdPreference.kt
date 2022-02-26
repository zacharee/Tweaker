package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.SettingsType

class StorageThresholdPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES,
            Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE
        )
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