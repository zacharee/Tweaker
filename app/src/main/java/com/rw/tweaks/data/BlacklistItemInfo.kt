package com.rw.tweaks.data


data class CustomBlacklistItemInfo(
    var label: CharSequence?,
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
}