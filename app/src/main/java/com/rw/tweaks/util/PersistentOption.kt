package com.rw.tweaks.util

data class PersistentOption(
    val type: SettingsType,
    val key: String
) {
    companion object {
        fun fromString(input: String): PersistentOption {
            val split = input.split(":")

            return PersistentOption(SettingsType.fromString(split[0]), split[1])
        }
    }

    override fun toString(): String {
        return "$type:$key"
    }
}