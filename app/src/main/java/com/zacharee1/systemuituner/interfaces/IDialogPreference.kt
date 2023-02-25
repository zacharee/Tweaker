package com.zacharee1.systemuituner.interfaces

interface IDialogPreference {
    suspend fun onValueChanged(newValue: Any?, key: String): Boolean
}