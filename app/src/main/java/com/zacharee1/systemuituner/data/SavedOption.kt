package com.zacharee1.systemuituner.data

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedOption(
    val type: SettingsType,
    val key: String,
    var value: String?
) : Parcelable {
    companion object {
        val gson: Gson = GsonBuilder().create()

        fun fromString(input: String): SavedOption {
            return try {
                gson.fromJson(
                    input,
                    object : TypeToken<SavedOption>() {}.type
                )
            } catch (e: Exception) {
                //Prevent crashes when options have been saved the old way
                val split = input.split(":")

                SavedOption(
                    SettingsType.fromString(
                        split[0]
                    ),
                    split[1],
                    split[2]
                )
            }
        }
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}