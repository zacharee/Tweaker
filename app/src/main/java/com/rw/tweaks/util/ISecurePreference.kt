package com.rw.tweaks.util

import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

interface ISecurePreference {
    var type: SettingsType
    var writeKey: String?
    var dangerous: Boolean
    var visibilityVerifier: BaseVisibilityVerifier?

    fun onValueChanged(newValue: Any?, key: String?)
}