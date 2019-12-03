package com.rw.tweaks.data

import android.graphics.drawable.Drawable

data class LoadedAppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val colorPrimary: Int,
    var isChecked: Boolean
) {
    fun matchesQuery(query: String?): Boolean {
        return query.isNullOrBlank()
                || label.contains(query, true)
                || packageName.contains(query, true)
    }
}