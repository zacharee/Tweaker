package com.rw.tweaks.util

import androidx.preference.Preference
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

    fun onValueChanged(newValue: Any?, key: String?)

    fun judgeEnabled(pref: Preference) {
        pref.isEnabled = (lowApi == API_UNDEFINED || api >= lowApi) && (highApi == API_UNDEFINED || api <= highApi)
    }

    fun init(pref: Preference)
}

class SecurePreference : ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous: Boolean = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null
    override var lowApi: Int = ISecurePreference.API_UNDEFINED
    override var highApi: Int = ISecurePreference.API_UNDEFINED

    override fun onValueChanged(newValue: Any?, key: String?) {}

    override fun init(pref: Preference) {
        judgeEnabled(pref)

        visibilityVerifier?.let {
            pref.isVisible = it.shouldShow
        }

        if (writeKey == null) writeKey = pref.key
    }
}