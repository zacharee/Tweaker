package com.zacharee1.systemuituner.util.verifiers

import android.content.Context
import com.zacharee1.systemuituner.util.hasSdCard
import com.zacharee1.systemuituner.util.isTouchWiz

abstract class BaseVisibilityVerifier(internal val context: Context) {
    abstract val shouldShow: Boolean
}

class ShowStorage(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = context.hasSdCard
}

class ShowClockPosition(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = context.isTouchWiz
}