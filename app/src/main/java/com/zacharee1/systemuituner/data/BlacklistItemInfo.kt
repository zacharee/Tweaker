package com.zacharee1.systemuituner.data


data class CustomBlacklistItemInfo(
    var label: String?,
    var key: String
) {
    companion object {
        fun fromString(input: String): CustomBlacklistItemInfo {
            val split = input.split(":")

            return CustomBlacklistItemInfo(split[0], split[1])
        }
    }

    override fun toString(): String {
        return "$label:$key"
    }

    override fun equals(other: Any?): Boolean {
        return other is CustomBlacklistItemInfo && other.key == key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}