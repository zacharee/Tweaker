package com.rw.tweaks.prefs.nav

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.rw.tweaks.R
import com.rw.tweaks.util.dpAsPx

open class NavigationPreference(context: Context, attributeSet: AttributeSet?) : Preference(context, attributeSet) {
    enum class StartMargin {
        NORMAL,
        INDENT
    }

    private var action: Int = 0

    internal open val startMargin = StartMargin.NORMAL
    internal open val useDividers = true

    init {
        isIconSpaceReserved = false

        if (attributeSet != null) {
            val array = context.theme.obtainStyledAttributes(attributeSet, R.styleable.NavigationPreference, 0, 0)

            action = array.getResourceId(R.styleable.NavigationPreference_navigation_action, 0)
        }
    }

    override fun onClick() {
        super.onClick()

        if (action != 0) {
            val context = this.context
            if (context is Activity) {
                context.findNavController(R.id.nav_host_fragment)
                    .navigate(action)
            }
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val dpNormal = context.dpAsPx(0)
        val dpIndent = context.dpAsPx(48)

        (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).apply {
            marginStart = if (startMargin == StartMargin.NORMAL) dpNormal else dpIndent

            holder.itemView.layoutParams = this
        }

        holder.isDividerAllowedAbove = useDividers
        holder.isDividerAllowedBelow = true
    }
}