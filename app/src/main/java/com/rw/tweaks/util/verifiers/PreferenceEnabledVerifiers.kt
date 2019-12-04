package com.rw.tweaks.util.verifiers

import android.content.Context
import com.rw.tweaks.R
import com.rw.tweaks.util.hasSdCard

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