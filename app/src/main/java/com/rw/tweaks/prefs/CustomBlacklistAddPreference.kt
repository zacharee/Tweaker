package com.rw.tweaks.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.rw.tweaks.R

class CustomBlacklistAddPreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    init {
        setTitle(R.string.icon_blacklist_add_custom)
        setSummary(R.string.icon_blacklist_add_custom_desc)
        key = "custom_blacklist_add_preference"

        isPersistent = false
        layoutResource = R.layout.custom_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.findViewById<View>(R.id.icon_frame).isVisible = false
    }
}