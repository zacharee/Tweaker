package com.zacharee1.systemuituner.compose.preferences

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.getStringByName
import com.zacharee1.systemuituner.util.isTouchWiz
import com.zacharee1.systemuituner.util.verifiers.EnableStorage
import com.zacharee1.systemuituner.views.UISounds
import java.io.File
import java.io.IOException

val Context.allScreens by com.zacharee1.systemuituner.util.lazy {
    appsScreen.prefs + audioScreen.prefs + developerScreen.prefs + displayScreen.prefs
}

val Context.appsScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_freeform),
            summary = resources.getString(R.string.feature_freeform_desc),
            key = "freeform_window",
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.DEVELOPMENT_ENABLE_FREEFORM_WINDOWS_SUPPORT),
            minApi = Build.VERSION_CODES.N,
            icon = R.drawable.window_restore,
            iconColor = R.color.pref_color_3
        ),
        ListPreferenceItem(
            title = resources.getString(R.string.feature_app_install_location),
            summary = resources.getString(R.string.feature_app_install_location_desc),
            key = "app_install_location",
            writeKey = Settings.Global.DEFAULT_INSTALL_LOCATION,
            options = arrayOf(
                ListPreferenceItem.Option(
                    resources.getString(R.string.option_app_install_location_auto),
                    "0",
                ),
                ListPreferenceItem.Option(
                    resources.getString(R.string.option_app_install_location_internal),
                    "1",
                ),
                ListPreferenceItem.Option(
                    resources.getString(R.string.option_app_install_location_sd),
                    "2"
                )
            ),
            defaultOption = "0",
            settingsType = SettingsType.GLOBAL,
            iconColor = R.color.pref_color_5,
            icon = R.drawable.ic_baseline_sd_card_24,
            enabled = {
                remember {
                    EnableStorage(this).shouldBeEnabled
                }
            },
        )
    ))
}

@OptIn(ExperimentalMaterial3Api::class)
val Context.audioScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        ListPreferenceItem(
            title = resources.getString(R.string.feature_disable_safe_audio_warning),
            summary = resources.getString(R.string.feature_disable_safe_audio_warning_desc),
            key = "safe_volume_state",
            defaultOption = "0",
            options = arrayOf(
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.safe_audio_state_not_configured),
                    value = "0",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.safe_audio_state_disabled),
                    value = "1",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.safe_audio_state_inactive),
                    value = "2",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.safe_audio_state_active),
                    value = "3",
                ),
            ),
            settingsType = SettingsType.GLOBAL,
            writeKey = Settings.Global.AUDIO_SAFE_VOLUME_STATE,
            icon = R.drawable.ic_baseline_volume_off_24,
            iconColor = R.color.pref_color_7,
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_custom_ui_sounds),
            summary = resources.getString(R.string.feature_custom_ui_sounds_desc),
            dangerous = true,
            key = "ui_sounds",
            icon = R.drawable.ic_baseline_phonelink_ring_24,
            iconColor = R.color.pref_color_6,
            dialogContents = {
                val context = LocalContext.current
                val settingsProviderResources = remember {
                    context.packageManager.getResourcesForApplication(
                        UISounds.PROVIDER_PKG
                    )
                }
                
                val items = remember {
                    arrayOf(
                        UISoundItem(
                            name = R.string.option_ui_sound_car_dock,
                            desc = R.string.option_ui_sound_car_dock_desc,
                            key = Settings.Global.CAR_DOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_car_dock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_car_undock,
                            desc = R.string.option_ui_sound_car_undock_desc,
                            key = Settings.Global.CAR_UNDOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_car_undock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_desk_dock,
                            desc = R.string.option_ui_sound_desk_dock_desc,
                            key = Settings.Global.DESK_DOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_desk_dock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_desk_undock,
                            desc = R.string.option_ui_sound_desk_undock_desc,
                            key = Settings.Global.DESK_UNDOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_desk_undock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_lock,
                            desc = R.string.option_ui_sound_lock_desc,
                            key = Settings.Global.LOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_lock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_unlock,
                            desc = R.string.option_ui_sound_unlock_desc,
                            key = Settings.Global.UNLOCK_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_unlock_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_low_battery,
                            desc = R.string.option_ui_sound_low_battery_desc,
                            key = Settings.Global.LOW_BATTERY_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_low_battery_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_trusted,
                            desc = R.string.option_ui_sound_trusted_desc,
                            key = Settings.Global.TRUSTED_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_trusted_sound", UISounds.PROVIDER_PKG)
                        ),
                        UISoundItem(
                            name = R.string.option_ui_sound_wireless_charging,
                            desc = R.string.option_ui_sound_wireless_charging_desc,
                            key = Settings.Global.WIRELESS_CHARGING_STARTED_SOUND,
                            settingsType = SettingsType.GLOBAL,
                            default = settingsProviderResources.getStringByName("def_wireless_charging_started_sound", UISounds.PROVIDER_PKG)
                        )
                    )
                }

                val itemStates = items.map { item ->
                    val state = context.rememberSettingsState(
                        key = item.settingsType to item.key,
                        value = { context.getSetting(item.settingsType, item.key, item.default) },
                        revertable = true,
                        saveOption = true,
                    )

                    Triple(
                        item,
                        state,
                        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                            result?.let { uri ->
                                val ext = contentResolver.getType(uri)?.split("/")?.getOrElse(1) { "ogg" } ?: "ogg"
                                val filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                                val folder = File(filesDir, "sounds")

                                folder.mkdirs()
                                folder.setReadable(true, false)
                                folder.setExecutable(true, false)

                                val dest = File(folder, "ui_sound_${item.key}.$ext")
                                if (dest.exists()) dest.delete()

                                try {
                                    dest.createNewFile()

                                    dest.outputStream().use { output ->
                                        contentResolver.openInputStream(uri).use { input ->
                                            input?.copyTo(output)
                                        }
                                    }

                                    dest.setReadable(true, false)
                                    dest.setExecutable(true, false)

                                    state.value = dest.absolutePath
                                } catch (e: IOException) {
                                    Log.e("SystemUITunerSystemSettings", "Error", e)
                                    Toast.makeText(context, resources.getString(R.string.error_creating_file_template, e.message), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
                
                @Suppress("DEPRECATION") 
                var chargingSoundEnabled by context.rememberBooleanSettingsState(
                    keys = arrayOf(
                        SettingsType.GLOBAL to Settings.Global.CHARGING_SOUNDS_ENABLED,
                        SettingsType.SECURE to Settings.Secure.CHARGING_SOUNDS_ENABLED,
                    ),
                    enabledValue = 1,
                    disabledValue = 0,
                    revertable = true,
                    saveOption = true,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item("charging_sound_enabled") {
                        OutlinedCard(onClick = { chargingSoundEnabled = !chargingSoundEnabled }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.option_ui_sound_disable_charging),
                                        style = MaterialTheme.typography.titleMedium,
                                    )

                                    Text(
                                        text = stringResource(id = R.string.option_ui_sound_disable_charging_desc)
                                    )
                                }

                                Switch(
                                    checked = !chargingSoundEnabled,
                                    onCheckedChange = { chargingSoundEnabled = !it }
                                )
                            }
                        }
                    }

                    items(items = itemStates, { it.first.hashCode() }) { (item, state, launcher) ->
                        OutlinedCard(onClick = { launcher.launch(arrayOf("audio/*")) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = { state.value = item.default }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_undo_black_24dp),
                                        contentDescription = stringResource(id = R.string.reset),
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = stringResource(id = item.name),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = stringResource(id = item.desc)
                                    )

                                    state.value?.let { value ->
                                        Spacer(modifier = Modifier.size(4.dp))

                                        Text(
                                            text = value,
                                            fontFamily = FontFamily.Monospace,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            saveOption = true,
            revertable = true,
        )
    ))
}

val Context.developerScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_enable_adb),
            summary = resources.getString(R.string.feature_enable_adb_desc),
            icon = R.drawable.ic_baseline_adb_24,
            iconColor = R.color.pref_color_3,
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.ADB_ENABLED),
            key = Settings.Global.ADB_ENABLED,
        )
    ))
}

val Context.displayScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SeekBarPreferenceItem(
            title = resources.getString(R.string.feature_lock_timeout),
            summary = resources.getString(R.string.feature_lock_timeout_desc),
            key = Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT,
            defaultValue = 5000,
            iconColor = R.color.pref_color_6,
            icon = R.drawable.progress_clock,
            writeKey = SettingsType.SECURE to Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT,
            minValue = 0,
            maxValue = Int.MAX_VALUE,
            unit = "ms",
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.feature_battery_saver_trigger),
            summary = resources.getString(R.string.feature_battery_saver_trigger_desc),
            key = Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL,
            defaultValue = 5,
            icon = R.drawable.ic_baseline_battery_alert_24,
            iconColor = R.color.pref_color_3,
            unit = "%",
            minValue = 0,
            maxValue = 100,
            writeKey = SettingsType.GLOBAL to Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.feature_font_scale),
            summary = resources.getString(R.string.feature_font_scale_desc),
            key = Settings.System.FONT_SCALE,
            defaultValue = 1.0,
            icon = R.drawable.ic_baseline_format_size_24,
            iconColor = R.color.pref_color_7,
            minValue = 0.2,
            maxValue = 3.0,
            scale = 0.01,
            writeKey = SettingsType.SYSTEM to Settings.System.FONT_SCALE,
        ),
        ListPreferenceItem(
            title = resources.getString(R.string.feature_custom_rotation),
            summary = resources.getString(R.string.feature_custom_rotation_desc),
            key = Settings.System.USER_ROTATION,
            defaultOption = "0",
            icon = R.drawable.ic_baseline_screen_lock_rotation_24,
            iconColor = R.color.pref_color_6,
            writeKey = Settings.System.USER_ROTATION,
            options = arrayOf(
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.option_user_rotation_0),
                    value = "0",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.option_user_rotation_90),
                    value = "1",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.option_user_rotation_180),
                    value = "2",
                ),
                ListPreferenceItem.Option(
                    label = resources.getString(R.string.option_user_rotation_270),
                    value = "3",
                ),
            ),
            settingsType = SettingsType.SYSTEM,
        ),
        // COLOR PICKER PREF
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_touchwiz_disable_high_brightness_warning),
            summary = resources.getString(R.string.option_touchwiz_disable_high_brightness_warning_desc),
            key = "shown_max_brightness_warning",
            writeKeys = arrayOf(SettingsType.SYSTEM to "shown_max_brightness_warning"),
            icon = R.drawable.ic_baseline_brightness_7_24,
            iconColor = R.color.pref_color_1,
            visible = { isTouchWiz }
        ),
        // NIGHT MODE PREF
    ))
}

val Context.netCellularScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_shortcode_warning),
            summary = resources.getString(R.string.feature_shortcode_warning_desc),
            key = Settings.Global.SMS_SHORT_CODE_CONFIRMATION,
            icon = R.drawable.ic_baseline_sms_failed_24,
            iconColor = R.color.pref_color_5,
            enabledValue = "false",
            disabledValue = "true",
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.SMS_SHORT_CODE_CONFIRMATION)
        ),
        // SMS LIMITS PREF
    ))
}

val Context.netWifiScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        // TETHERING PREF
    ))
}

val Context.netMiscScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        // AIRPLANE MODE PREF
    ))
}

data class Screen(val prefs: List<BasePreferenceItem>)

data class UISoundItem(
    @StringRes val name: Int,
    @StringRes val desc: Int,
    val key: String,
    val default: String,
    val settingsType: SettingsType,
)
