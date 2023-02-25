package com.zacharee1.systemuituner.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.IUISoundSelectionCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.UISoundSelector
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.UiSoundsBinding
import com.zacharee1.systemuituner.databinding.UiSoundsItemBinding
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.launch

class UISounds(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val PROVIDER_PKG = "com.android.providers.settings"
    }

    private val binding by lazy { UiSoundsBinding.bind(this) }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        context.prefManager.prefs.registerOnSharedPreferenceChangeListener(this)

        updateChargingSoundToggle()
        binding.disableChargingSound.setOnCheckedChangeListener { _, isChecked ->
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                context.writeSettingsBulk(
                    SettingsInfo(SettingsType.GLOBAL, Settings.Global.CHARGING_SOUNDS_ENABLED, if (isChecked) 0 else 1),
                    SettingsInfo(SettingsType.SECURE, Settings.Secure.CHARGING_SOUNDS_ENABLED, if (isChecked) 0 else 1),
                    revertable = true,
                    saveOption = true
                )
            }
        }

        binding.soundsList.adapter = Adapter(context)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        context.prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    @Suppress("DEPRECATION")
    private fun updateChargingSoundToggle() {
        binding.disableChargingSound.isChecked = context.getSetting(SettingsType.GLOBAL, Settings.Global.CHARGING_SOUNDS_ENABLED, 1) == "0"
                || context.getSetting(SettingsType.SECURE, Settings.Secure.CHARGING_SOUNDS_ENABLED, 1) == "0"
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PrefManager.SAVED_OPTIONS) {
            post {
                updateChargingSoundToggle()
                binding.soundsList.adapter?.notifyDataSetChanged()
            }
        }
    }

    inner class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.VH>() {
        private val settingsProviderResources = context.packageManager.getResourcesForApplication(PROVIDER_PKG)

        private val items = arrayListOf(
            SoundItemInfo(
                name = R.string.option_ui_sound_car_dock,
                desc = R.string.option_ui_sound_car_dock_desc,
                key = "car_dock_sound",
                default = settingsProviderResources.getStringByName("def_car_dock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_car_undock,
                desc = R.string.option_ui_sound_car_undock_desc,
                key = "car_undock_sound",
                default = settingsProviderResources.getStringByName("def_car_undock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_desk_dock,
                desc = R.string.option_ui_sound_desk_dock_desc,
                key = "desk_dock_sound",
                default = settingsProviderResources.getStringByName("def_desk_dock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_desk_undock,
                desc = R.string.option_ui_sound_desk_undock_desc,
                key = "desk_undock_sound",
                default = settingsProviderResources.getStringByName("def_desk_undock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_lock,
                desc = R.string.option_ui_sound_lock_desc,
                key = "lock_sound",
                default = settingsProviderResources.getStringByName("def_lock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_unlock,
                desc = R.string.option_ui_sound_unlock_desc,
                key = "unlock_sound",
                default = settingsProviderResources.getStringByName("def_unlock_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_low_battery,
                desc = R.string.option_ui_sound_low_battery_desc,
                key = "low_battery_sound",
                default = settingsProviderResources.getStringByName("def_low_battery_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_trusted,
                desc = R.string.option_ui_sound_trusted_desc,
                key = "trusted_sound",
                default = settingsProviderResources.getStringByName("def_trusted_sound", PROVIDER_PKG)
            ),
            SoundItemInfo(
                name = R.string.option_ui_sound_wireless_charging,
                desc = R.string.option_ui_sound_wireless_charging_desc,
                key = "wireless_charging_started_sound",
                default = settingsProviderResources.getStringByName("def_wireless_charging_started_sound", PROVIDER_PKG)
            )
        )

        private val callback = object : IUISoundSelectionCallback.Stub() {
            override fun onSoundSelected(uri: String, key: String) {
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    context.writeSetting(SettingsType.GLOBAL, key, uri,
                        revertable = true,
                        saveOption = true
                    )

                    val index = items.indexOfFirst { it.key == key }
                    notifyItemChanged(index)
                }
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
                val binding = UiSoundsItemBinding.bind(this)

                val info = items[position]

                binding.type.text = resources.getText(info.name)
                binding.desc.text = resources.getText(info.desc)
                binding.uri.text = context.getSetting(SettingsType.GLOBAL, info.key)
                if (info.icon != 0) binding.icon.setImageResource(info.icon)
                else binding.icon.isVisible = false

                setOnClickListener {
                    UISoundSelector.start(
                        context,
                        items[holder.bindingAdapterPosition].key,
                        callback
                    )
                }

                binding.reset.setOnClickListener {
                    val item = items[holder.bindingAdapterPosition]

                    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                        context.writeSetting(SettingsType.GLOBAL, item.key, item.default,
                            revertable = true,
                            saveOption = true
                        )
                        notifyItemChanged(holder.bindingAdapterPosition)
                    }
                }
            }
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view)
    }

    data class SoundItemInfo(
        val name: Int,
        val desc: Int,
        val key: String,
        val default: String?,
        val icon: Int = 0
    )
}