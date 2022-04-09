package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.DrawerHeaderBinding

class NavHeaderPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    var onSearchClickListener: (() -> Unit)? = null

    init {
        layoutResource = R.layout.widget_only_preference
        widgetLayoutResource = R.layout.drawer_header
        isEnabled = true
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        with(holder.itemView.findViewById<View>(R.id.search_enter)) {
            setOnClickListener {
                onSearchClickListener?.invoke()
            }
        }
    }
}