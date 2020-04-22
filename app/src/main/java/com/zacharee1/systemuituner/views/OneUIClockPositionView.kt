package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure
import kotlinx.android.synthetic.main.one_ui_clock_position.view.*

class OneUIClockPositionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    companion object {
        const val POSITION_LEFT = "left_clock_position"
        const val POSITION_MIDDLE = "middle_clock_position"
        const val POSITION_RIGHT = "right_clock_position"
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val blacklist = context.getSetting(SettingsType.SECURE, "icon_blacklist") ?: ""
        val currentPosition = when {
            blacklist.contains(POSITION_RIGHT) -> R.id.position_right
            blacklist.contains(POSITION_MIDDLE) -> R.id.position_middle
            else -> R.id.position_left
        }

        clock_position.check(currentPosition)
        clock_position.setOnCheckedChangeListener { _, checkedId ->
            val blacklistSet = HashSet((context.getSetting(SettingsType.SECURE, "icon_blacklist") ?: "").split(","))

            when (checkedId) {
                R.id.position_left -> {
                    blacklistSet.removeAll(arrayOf(POSITION_MIDDLE, POSITION_RIGHT))
                    blacklistSet.add(POSITION_LEFT)
                }
                R.id.position_middle -> {
                    blacklistSet.removeAll(arrayOf(POSITION_LEFT, POSITION_RIGHT))
                    blacklistSet.add(POSITION_MIDDLE)
                }
                R.id.position_right -> {
                    blacklistSet.removeAll(arrayOf(POSITION_LEFT, POSITION_MIDDLE))
                    blacklistSet.add(POSITION_RIGHT)
                }
            }

            context.prefManager.blacklistedItems = blacklistSet
            context.writeSecure("icon_blacklist", blacklistSet.joinToString(","))
        }
    }
}