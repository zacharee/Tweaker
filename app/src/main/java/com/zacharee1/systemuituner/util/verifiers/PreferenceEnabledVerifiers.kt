package com.zacharee1.systemuituner.util.verifiers

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.hasSdCard
import com.zacharee1.systemuituner.util.isTouchWiz

abstract class BasePreferenceEnabledVerifier(internal val context: Context) {
    abstract val shouldBeEnabled: Boolean
    abstract val message: CharSequence?
}

class EnableStorage(context: Context) : BasePreferenceEnabledVerifier(context) {
    override val shouldBeEnabled: Boolean
        get() = context.hasSdCard
    override val message: CharSequence?
        get() = context.resources.getString(R.string.compatibility_message_storage)
}

class EnableLockscreenShortcuts(context: Context) : BasePreferenceEnabledVerifier(context) {
    override val shouldBeEnabled: Boolean
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return true
            if (context.isTouchWiz) return true

            val resNames = arrayOf("config_keyguardShowLeftAffordance", "config_keyguardShowCameraAffordance")
            val remRes = try {
                context.packageManager.getResourcesForApplication("com.android.systemui")
            } catch (e: PackageManager.NameNotFoundException) {
                return true
            }

            return resNames.map {
                try {
                    remRes.getBoolean(remRes.getIdentifier(it, "bool", "com.android.systemui"))
                } catch (e: Resources.NotFoundException) {
                    // If the resource doesn't exist, just assume we can use the shortcuts.
                    true
                }
            }.all { it }
        }

    override val message: CharSequence?
        get() = null
}