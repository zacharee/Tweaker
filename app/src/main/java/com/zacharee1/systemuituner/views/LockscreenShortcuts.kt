package com.zacharee1.systemuituner.views

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.ILockscreenShortcutSelectedCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.LockscreenShortcutSelector
import com.zacharee1.systemuituner.databinding.LockscreenShortcutBinding
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure

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
                val binding = LockscreenShortcutBinding.bind(this)

                val item = items[position]
                val value = Settings.Secure.getString(context.contentResolver, item.key)
                val cName = ComponentName.unflattenFromString(value ?: "")

                binding.typeName.text = resources.getText(item.label)

                if (cName != null) {
                    binding.appIcon.setImageDrawable(
                        try {
                            context.packageManager.getApplicationIcon(cName.packageName)
                        } catch (e: PackageManager.NameNotFoundException) {
                            null
                        }
                    )

                    binding.appName.text = try {
                        context.packageManager.getActivityInfo(cName, 0).loadLabel(context.packageManager)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                    binding.component.text = cName.flattenToShortString()
                } else {
                    binding.appIcon.setImageDrawable(null)

                    binding.appName.text = null
                    binding.component.text = null
                }

                binding.reset.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]

                    context.prefManager.saveOption(SettingsType.SECURE, newInfo.key, null)
                    context.writeSecure(newInfo.key, null)
                    notifyItemChanged(holder.adapterPosition)
                }

                setOnClickListener {
                    val newInfo = items[holder.adapterPosition]

                    LockscreenShortcutSelector.start(context, newInfo.key, object : ILockscreenShortcutSelectedCallback.Stub() {
                        override fun onSelected(component: String?, key: String) {
                            context.prefManager.saveOption(SettingsType.SECURE, key, component)
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