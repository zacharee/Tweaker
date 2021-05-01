package com.zacharee1.systemuituner.interfaces

import com.zacharee1.systemuituner.util.SettingsType

interface ISpecificPreference {
    val keys: MutableMap<SettingsType, Array<String>>
}