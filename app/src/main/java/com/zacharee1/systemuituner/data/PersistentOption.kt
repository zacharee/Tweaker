package com.zacharee1.systemuituner.data

import com.zacharee1.systemuituner.util.SettingsType

class CustomPersistentOption(
    val label: String,
    val value: String?,
    type: SettingsType,
    key: String
) : PersistentOption(type, key) {
    companion object {
        fun fromString(input: String): CustomPersistentOption {
            val split = input.split(":")

            return CustomPersistentOption(
                split[0],
                split[1],
                SettingsType.fromString(
                    split[2]
                ),
                split[3]
            )
        }
    }

    override fun toString(): String {
        return "$label:$value:$type:$key"
    }

    override fun equals(other: Any?): Boolean {
        return other is CustomPersistentOption &&
                other.label == label &&
                other.type == type &&
                other.key == key
    }

    override fun hashCode(): Int {
        return label.hashCode() * super.hashCode()
    }
}

open class PersistentOption(
    val type: SettingsType,
    val key: String
) {
    companion object {
        fun fromString(input: String): PersistentOption {
            val split = input.split(":")

            return PersistentOption(
                SettingsType.fromString(
                    split[0]
                ),
                split[1]
            )
        }
    }

    override fun toString(): String {
        return "$type:$key"
    }

    override fun equals(other: Any?): Boolean {
        return other is PersistentOption &&
                other.type == type &&
                other.key == key
    }

    override fun hashCode(): Int {
        return type.hashCode() * key.hashCode()
    }
}