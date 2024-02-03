package com.zacharee1.systemuituner.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun Context.openShizuku() {
    try {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            `package` = "moe.shizuku.privileged.api"
        })
    } catch (e: ActivityNotFoundException) {
        openShizukuWebsite()
    }
}

fun Context.openShizukuWebsite() {
    launchUrl("https://shizuku.rikka.app")
}
