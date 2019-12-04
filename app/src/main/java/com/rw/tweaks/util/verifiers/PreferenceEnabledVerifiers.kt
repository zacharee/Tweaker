package com.rw.tweaks.util.verifiers

import android.content.Context
import com.rw.tweaks.util.hasSdCard

abstract class BasePreferenceEnabledVerifier(internal val context: Context) {
    abstract val shouldBeEnabled: Boolean
}

class EnableStorage(context: Context): BasePreferenceEnabledVerifier(context) {
    override val shouldBeEnabled: Boolean
        get() = context.hasSdCard
}