package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

class CameraGesturesPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null

    init {
        key = "camera_gestures"

        setTitle(R.string.feature_camera_gestures)
        setSummary(R.string.feature_camera_gestures_desc)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}