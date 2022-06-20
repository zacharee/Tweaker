package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure

open class BlacklistPreference(context: Context, attrs: AttributeSet?) : SwitchPreference(context, attrs), Preference.OnPreferenceChangeListener {
    private val additionalKeys by lazy { HashSet<String>() }
    private val allKeys: HashSet<String>
        get() = HashSet(additionalKeys).apply { add(autoWriteKey ?: key) }

    var autoWriteKey: String? = null

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

        val currentlyBlacklisted = HashSet(context.getSetting(SettingsType.SECURE, "icon_blacklist")?.split(",") ?: HashSet<String>())
        isChecked = !currentlyBlacklisted.containsAll(allKeys)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.findViewById<View>(R.id.icon_frame).isVisible = false
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        //no-op
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val isChecked = newValue.toString().toBoolean()

        val currentlyBlacklisted = HashSet(context.getSetting(SettingsType.SECURE, "icon_blacklist")?.split(",") ?: HashSet<String>())

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
        additionalKeys.removeAll(keys.toSet())
    }

    fun clearAdditionalKeys() {
        additionalKeys.clear()
    }
}