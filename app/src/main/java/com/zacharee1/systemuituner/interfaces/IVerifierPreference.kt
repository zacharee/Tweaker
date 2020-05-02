package com.zacharee1.systemuituner.interfaces

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.api
import com.zacharee1.systemuituner.util.apiToName
import com.zacharee1.systemuituner.util.verifiers.BasePreferenceEnabledVerifier
import com.zacharee1.systemuituner.util.verifiers.BaseVisibilityVerifier

interface IVerifierPreference {
    companion object {
        const val API_UNDEFINED = -1
    }

    var visibilityVerifier: BaseVisibilityVerifier?
    var enabledVerifier: BasePreferenceEnabledVerifier?
    var lowApi: Int
    var highApi: Int

    fun init(pref: Preference)
}

open class VerifierPreference(private val context: Context, attrs: AttributeSet?) : IVerifierPreference {
    override var visibilityVerifier: BaseVisibilityVerifier? = null
    override var enabledVerifier: BasePreferenceEnabledVerifier? = null
    override var lowApi: Int =
        IVerifierPreference.API_UNDEFINED
    override var highApi: Int =
        IVerifierPreference.API_UNDEFINED

    init {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.VerifiedPreference, 0, 0)

            lowApi = array.getInt(R.styleable.VerifiedPreference_low_api, lowApi)
            highApi = array.getInt(R.styleable.VerifiedPreference_high_api, highApi)

            val clazz = array.getString(R.styleable.VerifiedPreference_visibility_verifier)
            if (clazz != null) {
                visibilityVerifier = context.classLoader.loadClass(clazz)
                    .getConstructor(Context::class.java)
                    .newInstance(context) as BaseVisibilityVerifier
            }

            val enabledClazz = array.getString(R.styleable.VerifiedPreference_enabled_verifier)
            if (enabledClazz != null) {
                enabledVerifier = context.classLoader.loadClass(enabledClazz)
                    .getConstructor(Context::class.java)
                    .newInstance(context) as BasePreferenceEnabledVerifier
            }
        }
    }

    override fun init(pref: Preference) {
        val lowUndefined = lowApi == IVerifierPreference.API_UNDEFINED
        val highUndefined = highApi == IVerifierPreference.API_UNDEFINED

        pref.isEnabled = ((lowUndefined || api >= lowApi) && (highUndefined || api <= highApi)).also {
            if (!it) {
                val (toFormat, args) = when {
                    lowUndefined -> R.string.compatibility_message_higher to arrayOf(context.apiToName(highApi))
                    highUndefined -> R.string.compatibility_message_lower to arrayOf(context.apiToName(lowApi))
                    else -> R.string.compatibility_message_both to arrayOf(context.apiToName(lowApi), context.apiToName(highApi))
                }

                pref.summary = context.resources.getString(toFormat, *args)
            }
        } && (enabledVerifier?.shouldBeEnabled != false).also {
            if (!it) {
                pref.summary = enabledVerifier?.message
            }
        }

        visibilityVerifier?.let {
            pref.isVisible = it.shouldShow
        }
    }
}