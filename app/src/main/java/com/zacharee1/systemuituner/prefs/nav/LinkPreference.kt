package com.zacharee1.systemuituner.prefs.nav

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.launchUrl

class LinkPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    private val link: String?

    init {
        link = if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.LinkPreference, 0, 0)

            array.getString(R.styleable.LinkPreference_link)
        } else {
            null
        }
    }

    override fun onClick() {
        super.onClick()

        if (link != null) {
            context.launchUrl(link)
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.isDividerAllowedAbove = true
        holder.isDividerAllowedBelow = true
    }
}