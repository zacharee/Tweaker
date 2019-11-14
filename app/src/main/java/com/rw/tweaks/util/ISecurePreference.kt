package com.rw.tweaks.util

interface ISecurePreference {
    var type: SettingsType
    var writeKey: String?

    fun onValueChanged(newValue: Any?, key: String?)
}