package com.zacharee1.systemuituner.util.verifiers

import android.content.Context
import android.os.Build
import com.zacharee1.systemuituner.util.hasSdCard
import com.zacharee1.systemuituner.util.isTouchWiz

abstract class BaseVisibilityVerifier(internal val context: Context) {
    abstract val shouldShow: Boolean
}

class ShowStorage(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = context.hasSdCard
}

class ShowForTouchWiz(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = context.isTouchWiz
}

class ShowForAndroid10(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
}

class ShowForBelowAndroidNougat(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
}