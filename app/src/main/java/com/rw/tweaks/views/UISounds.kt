package com.rw.tweaks.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.rw.tweaks.IUISoundSelectionCallback
import com.rw.tweaks.R
import com.rw.tweaks.activities.UISoundSelector
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeGlobal
import kotlinx.android.synthetic.main.ui_sounds.view.*
import kotlinx.android.synthetic.main.ui_sounds_item.view.*

class UISounds(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        disable_charging_sound.isChecked = Settings.Global.getInt(context.contentResolver, Settings.Global.CHARGING_SOUNDS_ENABLED, 1) == 0
        disable_charging_sound.setOnCheckedChangeListener { _, isChecked ->
            context.prefManager.putInt(Settings.Global.CHARGING_SOUNDS_ENABLED, if (isChecked) 0 else 1)
            context.writeGlobal(Settings.Global.CHARGING_SOUNDS_ENABLED, if (isChecked) 0 else 1)
        }

        sounds_list.adapter = Adapter(context)
    }

    class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.VH>() {
        private val items = arrayListOf(
            SoundItemInfo(
                name = R.string.option_ui_sound_car_dock,
                desc = R.string.option_ui_sound_car_dock_desc,
                key = "car_dock_sound",
                default = "/system/media/audio/ui/Dock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_car_undock,
                desc = R.string.option_ui_sound_car_undock_desc,
                key = "car_undock_sound",
                default = "/system/media/audio/ui/Undock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_desk_dock,
                desc = R.string.option_ui_sound_desk_dock_desc,
                key = "desk_dock_sound",
                default = "/system/media/audio/ui/Dock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_desk_undock,
                desc = R.string.option_ui_sound_desk_undock_desc,
                key = "desk_undock_sound",
                default = "/system/media/audio/ui/Undock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_lock,
                desc = R.string.option_ui_sound_lock_desc,
                key = "lock_sound",
                default = "/system/media/audio/ui/Lock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_unlock,
                desc = R.string.option_ui_sound_unlock_desc,
                key = "unlock_sound",
                default = "/system/media/audio/ui/Unlock.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_low_battery,
                desc = R.string.option_ui_sound_low_battery_desc,
                key = "low_battery_sound",
                default = "/system/media/audio/ui/LowBattery.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_trusted,
                desc = R.string.option_ui_sound_trusted_desc,
                key = "trusted_sound",
                default = "/system/media/audio/ui/Trusted.ogg"
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_wireless_charging,
                desc = R.string.option_ui_sound_wireless_charging_desc,
                key = "wireless_charging_started_sound",
                default = "/system/media/audio/ui/ChargingStarted.ogg"
            )
        )

        private val callback = object : IUISoundSelectionCallback.Stub() {
            override fun onSoundSelected(uri: String, key: String) {
                context.prefManager.putString(key, uri)
                context.writeGlobal(key, uri)

                val index = items.indexOfFirst { it.key == key }
                notifyItemChanged(index)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.ui_sounds_item, parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                val info = items[position]

                type.text = resources.getText(info.name)
                desc.text = resources.getText(info.desc)
                uri.text = Settings.Global.getString(context.contentResolver, info.key)
                if (info.icon != 0) icon.setImageResource(info.icon)

                setOnClickListener {
                    UISoundSelector.start(
                        context,
                        items[holder.adapterPosition].key,
                        callback
                    )
                }

                reset.setOnClickListener {
                    val item = items[holder.adapterPosition]

                    context.prefManager.putString(item.key, item.default)
                    context.writeGlobal(item.key, item.default)
                    notifyItemChanged(holder.adapterPosition)
                }
            }
        }

        class VH(view: View) : RecyclerView.ViewHolder(view)
    }

    data class SoundItemInfo(
        val name: Int,
        val desc: Int,
        val key: String,
        val default: String?,
        val icon: Int = 0
    )
}