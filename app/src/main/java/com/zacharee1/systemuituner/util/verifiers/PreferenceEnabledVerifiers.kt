package com.zacharee1.systemuituner.util.verifiers

import android.content.Context
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.hasSdCard

abstract class BasePreferenceEnabledVerifier(internal val context: Context) {
    abstract val shouldBeEnabled: Boolean
    abstract val message: CharSequence?
}

class EnableStorage(context: Context): BasePreferenceEnabledVerifier(context) {
    override val shouldBeEnabled: Boolean
        get() = context.hasSdCard
    override val message: CharSequence?
        get() = context.resources.getString(R.string.compatibility_message_storage)
}