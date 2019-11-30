package com.rw.tweaks.util

import android.content.Context
import android.content.ContextWrapper
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

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

    fun onValueChanged(newValue: Any?, key: String?)

    fun init(pref: Preference)
}

interface ISpecificPreference {
    val keys: Array<String>
}

class SecurePreference(context: Context) : ContextWrapper(context), ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous: Boolean = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null
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
        }

        visibilityVerifier?.let {
            pref.isVisible = it.shouldShow
        }

        if (writeKey == null) writeKey = pref.key
    }
}