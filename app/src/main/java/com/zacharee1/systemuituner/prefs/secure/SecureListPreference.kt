package com.zacharee1.systemuituner.prefs.secure

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.content.edit
import androidx.core.content.res.TypedArrayUtils
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.interfaces.IListPreference
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.verifiers.BaseListPreferenceVerifier

@SuppressLint("RestrictedApi")
class SecureListPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs),
    IListPreference {
    private var verifier: BaseListPreferenceVerifier? = null

    private var setValue: Boolean = false
    override var value: String? = null
        set(value) {
            // Always persist/notify the first time.
            val changed = !TextUtils.equals(field, value)
            if (changed || !setValue) {
                field = value
                setValue = true
                try {
                    persistString(value)
                } catch (e: ClassCastException) {
                    preferenceManager.sharedPreferences.edit(true) {
                        remove(key)
                    }
                    persistString(value)
                }
                if (changed) {
                    notifyChanged()
                }
            }
        }

    override var entries: Array<CharSequence?>? = null
    override var entryValues: Array<CharSequence?>? = null

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

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }
}