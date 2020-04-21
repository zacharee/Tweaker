package com.zacharee1.systemuituner.interfaces

interface IDialogPreference {
    var writeKey: String?

    fun onValueChanged(newValue: Any?, key: String)
}