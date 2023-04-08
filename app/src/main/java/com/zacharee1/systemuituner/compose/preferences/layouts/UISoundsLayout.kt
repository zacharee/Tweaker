package com.zacharee1.systemuituner.compose.preferences.layouts

import android.annotation.SuppressLint
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.Composable
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
import com.zacharee1.systemuituner.compose.preferences.UISoundItem
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.getStringByName
import com.zacharee1.systemuituner.views.UISounds
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
@SuppressLint("SetWorldReadable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.UISoundsLayout() {
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
            ),
            UISoundItem(
                name = R.string.option_ui_sound_charging,
                desc = R.string.option_ui_sound_charging_desc,
                key = Settings.Global.CHARGING_STARTED_SOUND,
                settingsType = SettingsType.GLOBAL,
                default = settingsProviderResources.getStringByName("def_charging_started_sound", UISounds.PROVIDER_PKG)
            ),
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
                    val ext = context.contentResolver.getType(uri)?.split("/")?.getOrElse(1) { "ogg" } ?: "ogg"
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
                            context.contentResolver.openInputStream(uri).use { input ->
                                input?.copyTo(output)
                            }
                        }

                        dest.setReadable(true, false)
                        dest.setExecutable(true, false)

                        state.value = dest.absolutePath
                    } catch (e: IOException) {
                        Log.e("SystemUITunerSystemSettings", "Error", e)
                        Toast.makeText(context, context.resources.getString(R.string.error_creating_file_template, e.message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

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
}
