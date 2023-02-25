package com.zacharee1.systemuituner.views

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.ILockscreenShortcutSelectedCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.LockscreenShortcutSelector
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.LockscreenShortcutBinding
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.launch

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
                    val icon = if (cName.contains("NoUnlockNeeded/") && !cName.contains(".")) {
                        when {
                            cName.contains("Flashlight") -> ContextCompat.getDrawable(context, R.drawable.baseline_flashlight_on_24)
                            cName.contains("Dnd") -> ContextCompat.getDrawable(context, R.drawable.do_not_disturb)
                            else -> null
                        }
                    } else {
                        try {
                            context.packageManager.getApplicationIcon(ComponentName.unflattenFromString(cName)?.packageName)
                        } catch (e: PackageManager.NameNotFoundException) {
                            null
                        }
                    }

                    val label = if (cName.contains("NoUnlockNeeded/") && !cName.contains(".")) {
                        when {
                            cName.contains("Flashlight") -> resources.getString(R.string.flashlight)
                            cName.contains("Dnd") -> resources.getString(R.string.icon_blacklist_do_not_disturb)
                            else -> null
                        }
                    } else {
                        ComponentName.unflattenFromString(cName)?.let {
                            try {
                                context.packageManager.getActivityInfoCompat(it)
                                    .loadLabel(context.packageManager)
                            } catch (e: PackageManager.NameNotFoundException) {
                                null
                            }
                        }
                    }

                    binding.appIcon.setImageDrawable(icon)

                    binding.appName.text = label
                    binding.component.text = cName
                } else {
                    binding.appIcon.setImageDrawable(null)

                    binding.appName.text = null
                    binding.component.text = null
                }

                binding.reset.setOnClickListener {
                    val newInfo = items[holder.bindingAdapterPosition]

                    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                        newInfo.setComponentName(
                            context,
                            if (context.isTouchWiz) {
                                ShortcutInfo.ComponentValues.fromString(
                                    context.buildDefaultSamsungLockScreenShortcuts()
                                ).getForSide(item.side)
                            } else {
                                null
                            }
                        )
                        notifyItemChanged(holder.bindingAdapterPosition)
                    }
                }

                setOnClickListener {
                    val newInfo = items[holder.bindingAdapterPosition]

                    LockscreenShortcutSelector.start(context, newInfo.key, object : ILockscreenShortcutSelectedCallback.Stub() {
                        override fun onSelected(component: String?, key: String) {
                            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                                newInfo.setComponentName(context, component)

                                if (context.isTouchWiz) {
                                    notifyItemRangeChanged(0, items.size)
                                } else {
                                    notifyItemChanged(items.indexOfFirst { it.key == key })
                                }
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
            var left: String?,
            var right: String?
        ) {
            companion object {
                fun fromString(value: String?): ComponentValues {
                    if (value.isNullOrBlank() || !value.contains(";")) return ComponentValues(null, null)

                    val split = value.split(";")

                    if (split.size < 4) return ComponentValues(null, null)

                    return ComponentValues(
                        split[1],
                        split[3]
                    )
                }
            }

            fun toSettingsString(): String {
                return "1;${left};1;${right};"
            }

            fun getForSide(side: Side): String? {
                return when (side) {
                    Side.LEFT -> left
                    Side.RIGHT -> right
                }
            }

            fun setForSide(side: Side, newName: String?) {
                when (side) {
                    Side.LEFT -> left = newName
                    Side.RIGHT -> right = newName
                }
            }
        }

        fun getComponentName(context: Context): String? {
            return if (context.isTouchWiz) {
                val values = ComponentValues.fromString(context.getSetting(SettingsType.SYSTEM, key))

                values.getForSide(side)
            } else {
                context.getSetting(SettingsType.SECURE, key)
            }
        }

        suspend fun setComponentName(context: Context, newName: String?) {
            if (context.isTouchWiz) {
                val current = ComponentValues.fromString(context.getSetting(SettingsType.SYSTEM, key))
                current.setForSide(side, newName)

                val string = current.toSettingsString()

                context.writeSetting(SettingsType.SYSTEM, key, string, saveOption = true)
            } else {
                context.writeSetting(SettingsType.SECURE, key, newName, saveOption = true)
            }
        }
    }
}