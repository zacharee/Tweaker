package com.rw.tweaks.util.verifiers

import android.content.Context
import com.rw.tweaks.util.hasSdCard

abstract class BaseVisibilityVerifier(internal val context: Context) {
    abstract val shouldShow: Boolean
}

class ShowStorage(context: Context) : BaseVisibilityVerifier(context) {
    override val shouldShow: Boolean
        get() = context.hasSdCard
}