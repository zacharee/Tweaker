package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import android.os.UserHandle
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper

val Context.hasWss: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

val Context.hasDump: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED

val Context.hasPackageUsageStats: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED

val Context.hasShizukuPermission: Boolean
    get() = if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
        checkCallingOrSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
    } else {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

