package com.rw.tweaks.views

import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rw.tweaks.ILockscreenShortcutSelectedCallback
import com.rw.tweaks.R
import com.rw.tweaks.activities.LockscreenShortcutSelector
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeSecure
import kotlinx.android.synthetic.main.lockscreen_shortcut.view.*

class LockscreenShortcuts(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    init {
        adapter = Adapter()
    }

    class Adapter : RecyclerView.Adapter<Adapter.VH>() {
        private val items = arrayListOf(
            ShortcutInfo(
                R.string.option_lockscreen_shortcut_left,
                "sysui_keyguard_left"
            ),
            ShortcutInfo(
                R.string.option_lockscreen_shortcut_right,
                "sysui_keyguard_right"
            )
        )

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.lockscreen_shortcut, parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                val item = items[position]
                val value = Settings.Secure.getString(context.contentResolver, item.key)

                type_name.text = resources.getText(item.label)

                if (value != null) {
                    val cName = ComponentName.unflattenFromString(value)

                    app_icon.setImageDrawable(
                        try {
                            context.packageManager.getApplicationIcon(cName.packageName)
                        } catch (e: Resources.NotFoundException) {
                            null
                        }
                    )

                    app_name.text = context.packageManager.getActivityInfo(cName, 0).loadLabel(context.packageManager)
                    component.text = cName.flattenToShortString()
                } else {
                    app_icon.setImageDrawable(null)

                    app_name.text = null
                    component.text = null
                }

                reset.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]

                    context.prefManager.putString(newInfo.key, null)
                    context.writeSecure(newInfo.key, null)
                    notifyItemChanged(holder.adapterPosition)
                }

                setOnClickListener {
                    val newInfo = items[holder.adapterPosition]

                    LockscreenShortcutSelector.start(context, newInfo.key, object : ILockscreenShortcutSelectedCallback.Stub() {
                        override fun onSelected(component: String?, key: String) {
                            context.prefManager.putString(key, component)
                            context.writeSecure(key, component)
                            notifyItemChanged(items.indexOfFirst { it.key == key })
                        }
                    })
                }
            }
        }

        class VH(view: View) : ViewHolder(view)
    }

    data class ShortcutInfo(
        val label: Int,
        val key: String
    )
}