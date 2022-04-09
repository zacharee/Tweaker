package com.zacharee1.systemuituner.prefs.nav

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.dpAsPx

open class NavigationPreference(context: Context, attributeSet: AttributeSet?) :
    Preference(context, attributeSet) {
    enum class StartMargin {
        NORMAL,
        INDENT
    }

    var action: Int = 0

    val navController: NavController
        get() = (context as Activity).findNavController(R.id.nav_host_fragment)

    val destId: Int
        get() = navController.run {
            graph.getAction(action)?.destinationId ?: action
        }

    internal open val startMargin = StartMargin.NORMAL
    internal open val useDividers = true

    init {
        isIconSpaceReserved = false

        if (attributeSet != null) {
            val array = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.NavigationPreference,
                0,
                0
            )

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