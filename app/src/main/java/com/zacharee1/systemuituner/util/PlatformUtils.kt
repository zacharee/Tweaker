package com.zacharee1.systemuituner.util

import android.content.Context
import android.os.SystemProperties

val Context.isTouchWiz: Boolean
    get() = packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")

val isHTC: Boolean
    get() = !SystemProperties.get("ro.build.sense.version").isNullOrBlank()

val isLG: Boolean
    get() = !SystemProperties.get("ro.lge.lguiversion").isNullOrBlank()

val isHuawei: Boolean
    get() = !SystemProperties.get("ro.build.hw_emui_api_level").isNullOrBlank()

val isXiaomi: Boolean
    get() = !SystemProperties.get("ro.miui.ui.version.code").isNullOrBlank()