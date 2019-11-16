package com.rw.tweaks.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rw.tweaks.IImmersiveSelectionCallback
import com.rw.tweaks.activities.ImmersiveListSelector
import com.rw.tweaks.util.ImmersiveManager
import kotlinx.android.synthetic.main.immersive_mode.view.*

class ImmersiveMode(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val immersiveManager = ImmersiveManager(context)
    private val immersiveInfo = immersiveManager.parseAdvancedImmersive()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        advanced_full_all.isChecked = immersiveInfo.allFull
        advanced_status_all.isChecked = immersiveInfo.allStatus
        advanced_nav_all.isChecked = immersiveInfo.allNav

        advanced_full_all.setOnCheckedChangeListener { _, isChecked ->
            immersiveInfo.allFull = isChecked
            update()
        }
        advanced_status_all.setOnCheckedChangeListener { _, isChecked ->
            immersiveInfo.allStatus = isChecked
            update()
        }
        advanced_nav_all.setOnCheckedChangeListener { _, isChecked ->
            immersiveInfo.allNav = isChecked
            update()
        }

        advanced_full_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.fullApps, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.fullApps.clear()
                immersiveInfo.fullApps.addAll(it)
                update()
            })
        }
        advanced_status_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.statusApps, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.statusApps.clear()
                immersiveInfo.statusApps.addAll(it)
                update()
            })
        }
        advanced_nav_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.navApps, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.navApps.clear()
                immersiveInfo.navApps.addAll(it)
                update()
            })
        }

        advanced_full_blacklist_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.fullBl, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.fullBl.clear()
                immersiveInfo.fullBl.addAll(it)
                update()
            })
        }
        advanced_status_blacklist_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.statusBl, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.statusBl.clear()
                immersiveInfo.statusBl.addAll(it)
                update()
            })
        }
        advanced_nav_blacklist_select.setOnClickListener {
            ImmersiveListSelector.start(context, immersiveInfo.navApps, ImmersiveSelectionCallbackWrapper {
                immersiveInfo.navApps.clear()
                immersiveInfo.navApps.addAll(it)
                update()
            })
        }
    }

    private fun update() {
        immersiveManager.setAdvancedImmersive(immersiveInfo)
    }

    private class ImmersiveSelectionCallbackWrapper(private val callback: (checked: List<String>) -> Unit) : IImmersiveSelectionCallback.Stub() {
        override fun onImmersiveResult(checked: MutableList<Any?>) {
            callback(checked.map { it.toString() })
        }
    }
}