package com.rw.tweaks.util

interface ISecurePreference {
    var type: SettingsType
    var writeKey: String?
    var dangerous: Boolean

    fun onValueChanged(newValue: Any?, key: String?)
}