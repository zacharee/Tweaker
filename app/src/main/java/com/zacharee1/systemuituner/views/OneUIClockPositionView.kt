package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.OneUiClockPositionBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting

class OneUIClockPositionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs), IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null

    companion object {
        const val POSITION_LEFT = "left_clock_position"
        const val POSITION_MIDDLE = "middle_clock_position"
        const val POSITION_RIGHT = "right_clock_position"
    }

    private val binding by lazy { OneUiClockPositionBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val blacklist = context.getSetting(SettingsType.SECURE, "icon_blacklist") ?: ""
        val currentPosition = when {
            blacklist.contains(POSITION_RIGHT) -> R.id.position_right
            blacklist.contains(POSITION_MIDDLE) -> R.id.position_middle
            else -> R.id.position_left
        }

        binding.clockPosition.check(currentPosition)
        binding.clockPosition.setOnCheckedChangeListener { _, checkedId ->
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

            callback?.invoke(blacklistSet.joinToString(","))
        }
    }
}