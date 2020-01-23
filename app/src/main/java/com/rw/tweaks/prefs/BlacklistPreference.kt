package com.rw.tweaks.prefs

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ColorPreference
import com.rw.tweaks.util.IColorPreference
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeSecure

open class BlacklistPreference(context: Context, attrs: AttributeSet?) : SwitchPreference(context, attrs), IColorPreference by ColorPreference(context, attrs), Preference.OnPreferenceChangeListener {
    private val additionalKeys by lazy { HashSet<String>() }
    private val allKeys: HashSet<String>
        get() = HashSet(additionalKeys).apply { add(key) }

    init {
        isPersistent = false
        layoutResource = R.layout.custom_preference

        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.BlacklistPreference, 0, 0)

            array.getString(R.styleable.BlacklistPreference_additional_keys)?.let {
                additionalKeys.addAll(it.split(","))
            }
        }
    }

    override fun onAttached() {
        super.onAttached()
        super.setOnPreferenceChangeListener(this)

        val currentlyBlacklisted = HashSet(Settings.Secure.getString(context.contentResolver, "icon_blacklist")?.split(",") ?: HashSet<String>())
        isChecked = !currentlyBlacklisted.containsAll(allKeys)
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        //no-op
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any): Boolean {
        val isChecked = newValue.toString().toBoolean()

        val currentlyBlacklisted = HashSet(Settings.Secure.getString(context.contentResolver, "icon_blacklist")?.split(",") ?: HashSet<String>())

        if (!isChecked) {
            currentlyBlacklisted.addAll(allKeys)
        } else {
            currentlyBlacklisted.removeAll(allKeys)
        }

        context.prefManager.blacklistedItems = currentlyBlacklisted
        context.writeSecure("icon_blacklist", currentlyBlacklisted.joinToString(","))

        return true
    }

    fun addAdditionalKeys(keys: Collection<String>) {
        additionalKeys.addAll(keys)
    }

    fun removeAdditionalKeys(keys: Collection<String>) {
        additionalKeys.removeAll(keys)
    }

    fun clearAdditionalKeys() {
        additionalKeys.clear()
    }
}