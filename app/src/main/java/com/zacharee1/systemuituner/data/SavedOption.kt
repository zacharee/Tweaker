package com.zacharee1.systemuituner.data

import com.zacharee1.systemuituner.util.SettingsType

data class SavedOption(
    val type: SettingsType,
    val key: String,
    var value: String?
) {
    companion object {
        fun fromString(input: String): SavedOption {
            val split = input.split(":")

            return SavedOption(
                SettingsType.fromString(
                    split[0]
                ),
                split[1],
                split[2]
            )
        }
    }

    override fun toString(): String {
        return "$type:$key:${value ?: ""}"
    }
}