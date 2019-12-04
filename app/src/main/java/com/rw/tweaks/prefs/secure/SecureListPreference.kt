package com.rw.tweaks.prefs.secure

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.base.BaseSecurePreference
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.verifiers.BaseListPreferenceVerifier
import com.rw.tweaks.util.writeSetting

@SuppressLint("RestrictedApi")
class SecureListPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs) {
    private var verifier: BaseListPreferenceVerifier? = null
    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    private var setValue: Boolean = false
    var value: String? = null
        set(value) {
            // Always persist/notify the first time.
            val changed = !TextUtils.equals(field, value)
            if (changed || !setValue) {
                field = value
                setValue = true
                persistString(value)
                if (changed) {
                    notifyChanged()
                }
            }
        }

    var entries: Array<CharSequence?>? = null
    var entryValues: Array<CharSequence?>? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureListPreference, 0, 0)

        array.getString(R.styleable.SecureListPreference_verifier)?.let {
            verifier = context.classLoader.loadClass(it)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseListPreferenceVerifier

            verifier!!.verifyEntries(entries, entryValues).apply {
                entries = first
                entryValues = second
            }
        }

        entries = TypedArrayUtils.getTextArray(
            array, androidx.preference.R.styleable.ListPreference_entries,
            androidx.preference.R.styleable.ListPreference_android_entries
        )

        entryValues = TypedArrayUtils.getTextArray(
            array, androidx.preference.R.styleable.ListPreference_entryValues,
            androidx.preference.R.styleable.ListPreference_android_entryValues
        )

        array.recycle()
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        value = context.getSetting(type, key) ?: defaultValue?.toString() ?: entryValues!![0].toString()
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        _onPreferenceChangeListener = onPreferenceChangeListener
    }

    override fun getOnPreferenceChangeListener(): OnPreferenceChangeListener? {
        return _onPreferenceChangeListener
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onValueChanged(newValue: Any?, key: String?) {
        context.prefManager.putString(writeKey!!, newValue.toString())
        context.writeSetting(type, writeKey, newValue.toString().toInt())
    }

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
}