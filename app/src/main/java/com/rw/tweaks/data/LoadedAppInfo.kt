package com.rw.tweaks.data

import android.graphics.drawable.Drawable

data class LoadedAppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    var isChecked: Boolean
)