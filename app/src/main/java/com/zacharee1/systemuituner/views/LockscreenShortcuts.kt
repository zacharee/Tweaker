package com.zacharee1.systemuituner.views

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.ILockscreenShortcutSelectedCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.LockscreenShortcutSelector
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.LockscreenShortcutBinding
import com.zacharee1.systemuituner.util.*

class LockscreenShortcuts(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    init {
        adapter = Adapter(context)
    }

    class Adapter(context: Context) : RecyclerView.Adapter<Adapter.VH>() {
        private val items = arrayListOf(
            ShortcutInfo(
                R.string.option_lockscreen_shortcut_left,
                if (context.isTouchWiz) "lock_application_shortcut" else "sysui_keyguard_left",
                ShortcutInfo.Side.LEFT
            ),
            ShortcutInfo(
                R.string.option_lockscreen_shortcut_right,
                if (context.isTouchWiz) "lock_application_shortcut" else "sysui_keyguard_right",
                ShortcutInfo.Side.RIGHT
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
                val cName = item.getComponentName(context)

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
                    val newInfo = items[holder.bindingAdapterPosition]

//                    context.prefManager.saveOption(SettingsType.SECURE, newInfo.key, null)
//                    context.writeSetting(SettingsType.SECURE, newInfo.key, null)

                    newInfo.setComponentName(context, null)
                    notifyItemChanged(holder.bindingAdapterPosition)
                }

                setOnClickListener {
                    val newInfo = items[holder.bindingAdapterPosition]

                    LockscreenShortcutSelector.start(context, newInfo.key, object : ILockscreenShortcutSelectedCallback.Stub() {
                        override fun onSelected(component: String?, key: String) {
//                            context.prefManager.saveOption(SettingsType.SECURE, key, component)
//                            context.writeSetting(SettingsType.SECURE, key, component)

                            newInfo.setComponentName(context, ComponentName.unflattenFromString(component))

                            if (context.isTouchWiz) {
                                notifyItemRangeChanged(0, items.size)
                            } else {
                                notifyItemChanged(items.indexOfFirst { it.key == key })
                            }
                        }
                    })
                }
            }
        }

        class VH(view: View) : ViewHolder(view)
    }

    data class ShortcutInfo(
        val label: Int,
        val key: String,
        val side: Side
    ) {
        enum class Side {
            LEFT,
            RIGHT
        }

        data class ComponentValues(
            var left: ComponentName?,
            var right: ComponentName?
        ) {
            companion object {
                fun fromString(value: String?): ComponentValues {
                    if (value.isNullOrBlank() || !value.contains(";")) return ComponentValues(null, null)

                    val split = value.split(";")

                    if (split.size < 5) return ComponentValues(null, null)

                    return ComponentValues(
                        ComponentName.unflattenFromString(split[1]),
                        ComponentName.unflattenFromString(split[3])
                    )
                }
            }

            fun toSettingsString(): String {
                return "1;${left?.flattenToString()};1;${right?.flattenToString()};"
            }

            fun getForSide(side: Side): ComponentName? {
                return when (side) {
                    Side.LEFT -> left
                    Side.RIGHT -> right
                }
            }

            fun setForSide(side: Side, newName: ComponentName?) {
                when (side) {
                    Side.LEFT -> left = newName
                    Side.RIGHT -> right = newName
                }
            }
        }

        fun getComponentName(context: Context): ComponentName? {
            return if (context.isTouchWiz) {
                val values = ComponentValues.fromString(context.getSetting(SettingsType.SYSTEM, key))

                values.getForSide(side)
            } else {
                context.getSetting(SettingsType.SECURE, key)?.run {
                    ComponentName.unflattenFromString(this)
                }
            }
        }

        fun setComponentName(context: Context, newName: ComponentName?) {
            if (context.isTouchWiz) {
                val current = ComponentValues.fromString(context.getSetting(SettingsType.SYSTEM, key))
                current.setForSide(side, newName)

                val string = current.toSettingsString()

                context.prefManager.saveOption(SettingsType.SYSTEM, key, string)
                context.writeSetting(SettingsType.SYSTEM, key, string)
            } else {
                context.prefManager.saveOption(SettingsType.SECURE, key, newName?.flattenToString())
                context.writeSetting(SettingsType.SECURE, key, newName?.flattenToString())
            }
        }
    }
}