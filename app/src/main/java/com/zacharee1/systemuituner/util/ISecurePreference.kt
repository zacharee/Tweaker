package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.verifiers.BasePreferenceEnabledVerifier
import com.zacharee1.systemuituner.util.verifiers.BaseVisibilityVerifier

interface IDialogPreference {
    var writeKey: String?

    fun onValueChanged(newValue: Any?, key: String)
}

interface IListPreference : IDialogPreference {
    val entries: Array<CharSequence?>?
    val entryValues: Array<CharSequence?>?
    var value: String?

    fun findIndexOfValue(value: String?): Int {
        if (value != null && entryValues != null) {
            for (i in entryValues!!.indices.reversed()) {
                if (entryValues!!.get(i) == value) {
                    return i
                }
            }
        }
        return -1
    }
    fun callChangeListener(newValue: Any?): Boolean
}

interface ISecurePreference {
    companion object {
        const val API_UNDEFINED = -1
    }

    var type: SettingsType
    var writeKey: String?
    var dangerous: Boolean
    var visibilityVerifier: BaseVisibilityVerifier?
    var lowApi: Int
    var highApi: Int
    var enabledVerifier: BasePreferenceEnabledVerifier?

    fun init(pref: Preference)
}

interface ISpecificPreference {
    val keys: Array<String>
}

class SecurePreference(context: Context, attrs: AttributeSet?) : ContextWrapper(context), ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous: Boolean = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null
    override var enabledVerifier: BasePreferenceEnabledVerifier? = null
    override var lowApi: Int = ISecurePreference.API_UNDEFINED
    override var highApi: Int = ISecurePreference.API_UNDEFINED

    init {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.BaseSecurePreference, 0, 0)

            type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.BaseSecurePreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
            writeKey = array.getString(R.styleable.BaseSecurePreference_differing_key)
            dangerous = array.getBoolean(R.styleable.BaseSecurePreference_dangerous, false)
            lowApi = array.getInt(R.styleable.BaseSecurePreference_low_api, lowApi)
            highApi = array.getInt(R.styleable.BaseSecurePreference_high_api, highApi)

            val clazz = array.getString(R.styleable.BaseSecurePreference_visibility_verifier)
            if (clazz != null) {
                visibilityVerifier = context.classLoader.loadClass(clazz)
                    .getConstructor(Context::class.java)
                    .newInstance(context) as BaseVisibilityVerifier
            }

            val enabledClazz = array.getString(R.styleable.BaseSecurePreference_enabled_verifier)
            if (enabledClazz != null) {
                enabledVerifier = context.classLoader.loadClass(enabledClazz)
                    .getConstructor(Context::class.java)
                    .newInstance(context) as BasePreferenceEnabledVerifier
            }

            array.recycle()
        }
    }

    override fun init(pref: Preference) {
        val lowUndefined = lowApi == ISecurePreference.API_UNDEFINED
        val highUndefined = highApi == ISecurePreference.API_UNDEFINED

        pref.isEnabled = ((lowUndefined || api >= lowApi) && (highUndefined || api <= highApi)).also {
            if (!it) {
                val (toFormat, args) = when {
                    lowUndefined -> R.string.compatibility_message_higher to arrayOf(apiToName(highApi))
                    highUndefined -> R.string.compatibility_message_lower to arrayOf(apiToName(lowApi))
                    else -> R.string.compatibility_message_both to arrayOf(apiToName(lowApi), apiToName(highApi))
                }

                pref.summary = resources.getString(toFormat, *args)
            }
        } && (enabledVerifier?.shouldBeEnabled != false).also {
            if (!it) {
                pref.summary = enabledVerifier?.message
            }
        }

        visibilityVerifier?.let {
            pref.isVisible = it.shouldShow
        }

        if (writeKey == null) writeKey = pref.key
    }
}