package com.rw.tweaks.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.drawable.StateListDrawable
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.rw.tweaks.R
import com.rw.tweaks.util.verifiers.BasePreferenceEnabledVerifier
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier
import kotlinx.android.synthetic.main.custom_preference.view.*

interface ISecurePreference {
    companion object {
        const val API_UNDEFINED = -1
    }

    var type: SettingsType
    var writeKey: String?
    var dangerous: Boolean
    var visibilityVerifier: BaseVisibilityVerifier?
    var lowApi: Int
    var highApi: Int
    var iconColor: Int
    var enabledVerifier: BasePreferenceEnabledVerifier?

    fun onValueChanged(newValue: Any?, key: String?)

    fun init(pref: Preference)
    fun bindVH(holder: PreferenceViewHolder)
}

interface ISpecificPreference {
    val keys: Array<String>
}

class SecurePreference(context: Context) : ContextWrapper(context), ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous: Boolean = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null
    override var enabledVerifier: BasePreferenceEnabledVerifier? = null
    override var lowApi: Int = ISecurePreference.API_UNDEFINED
    override var highApi: Int = ISecurePreference.API_UNDEFINED
    override var iconColor: Int = Int.MIN_VALUE

    override fun onValueChanged(newValue: Any?, key: String?) {}

    override fun init(pref: Preference) {
        val lowUndefined = lowApi == ISecurePreference.API_UNDEFINED
        val highUndefined = highApi == ISecurePreference.API_UNDEFINED

        pref.isEnabled = ((lowUndefined || api >= lowApi) && (highUndefined || api <= highApi)).also {
            if (!it) {
                val (toFormat, args) = when {
                    lowUndefined -> R.string.compatibility_message_higher to arrayOf(apiToName(highApi))
                    highUndefined -> R.string.compatibility_message_lower to arrayOf(apiToName(lowApi))
                    else -> R.string.compatibility_message_both to arrayOf(apiToName(lowApi), apiToName(highApi))
                }

                pref.summary = resources.getString(toFormat, *args)
            }
        } && (enabledVerifier?.shouldBeEnabled != false).also {
            if (!it) {
                pref.summary = enabledVerifier?.message
            }
        }

        visibilityVerifier?.let {
            pref.isVisible = it.shouldShow
        }

        if (writeKey == null) writeKey = pref.key
    }

    override fun bindVH(holder: PreferenceViewHolder) {
        holder.itemView.icon_frame.apply {
            (background as StateListDrawable).apply {
                val drawable = getStateDrawable(1)

                if (iconColor != Int.MIN_VALUE) {
                    drawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP)
                } else {
                    drawable.clearColorFilter()
                }
            }
        }
    }
}