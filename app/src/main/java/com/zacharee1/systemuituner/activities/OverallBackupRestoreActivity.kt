package com.zacharee1.systemuituner.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.gson.reflect.TypeToken
import com.zacharee1.systemuituner.compose.preferences.PreferenceItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.preferences.BasePreference
import com.zacharee1.systemuituner.compose.preferences.BasePreferenceItem
import com.zacharee1.systemuituner.compose.preferences.BaseSettingsPreference
import com.zacharee1.systemuituner.compose.preferences.SettingsPreferenceItem
import com.zacharee1.systemuituner.data.SavedOption
import com.zacharee1.systemuituner.util.BugsnagUtils
import com.zacharee1.systemuituner.util.PersistenceHandlerRegistry
import com.zacharee1.systemuituner.util.listSettings
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting
import kotlinx.coroutines.launch

class OverallBackupRestoreActivity : AppCompatActivity() {
    private var backupData: HashSet<SavedOption>? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()

            val saveLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json")) { outputUri ->
                val data = backupData

                if (outputUri == null || data == null) {
                    return@rememberLauncherForActivityResult
                }

                contentResolver.openOutputStream(outputUri)?.bufferedWriter()?.use { output ->
                    output.write(SavedOption.gson.toJson(data))
                }
            }

            val restoreLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { inputUri ->
                if (inputUri == null) {
                    return@rememberLauncherForActivityResult
                }

                try {
                    val contents = contentResolver.openInputStream(inputUri)?.bufferedReader()?.use {
                        SavedOption.gson.fromJson<HashSet<SavedOption>>(it, object : TypeToken<HashSet<SavedOption>>(){}.type) ?: hashSetOf()
                    } ?: hashSetOf()

                    scope.launch {
                        val failedOptions = hashSetOf<SavedOption>()

                        contents.forEach { option ->
                            if (!writeSetting(option.type, option.key, option.value)) {
                                failedOptions.add(option)
                            }
                        }
                    }
                } catch (e: Throwable) {
                    Log.e("SystemUITuner", "Error restoring", e)
                    BugsnagUtils.notify(e)
                }
            }

            val items: List<BasePreferenceItem> = remember {
                listOf(
                    PreferenceItem(
                        title = resources.getString(R.string.back_up_all_settings),
                        summary = resources.getString(R.string.back_up_all_settings_desc),
                        key = "back_up_all_settings",
                        onClick = {
                            backupData = listSettings()?.let {
                                HashSet(it.toList())
                            }
                        },
                    ),
                    PreferenceItem(
                        title = resources.getString(R.string.back_up_changed_settings),
                        summary = resources.getString(R.string.back_up_changed_settings_desc),
                        key = "back_up_changed_settings",
                        onClick = {
                            backupData = HashSet(
                                prefManager.savedOptions + PersistenceHandlerRegistry.handlers.map { handler ->
                                    SavedOption(handler.settingsType, handler.settingsKey, handler.getPreferenceValueAsString())
                                },
                            )
                            saveLauncher.launch("SystemUITunerBackup.json")
                        },
                        icon = R.drawable.ic_baseline_save_24,
                        iconColor = R.color.pref_color_3,
                    ),
                    PreferenceItem(
                        title = resources.getString(R.string.restore_settings),
                        summary = resources.getString(R.string.restore_settings_desc),
                        key = "restore_settings",
                        onClick = {
                            restoreLauncher.launch(arrayOf("application/json"))
                        },
                        icon = R.drawable.ic_baseline_restore_24,
                        iconColor = R.color.pref_color_2,
                    ),
                )
            }

            Mdc3Theme {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .imePadding(),
                    ) {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(id = R.string.backup_restore))
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.go_back),
                                    )
                                }
                            },
                        )

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(items = items, key = { it.key }) { pref ->
                                when (pref) {
                                    is SettingsPreferenceItem -> {
                                        BaseSettingsPreference(info = pref)
                                    }
                                    is PreferenceItem -> {
                                        BasePreference(info = pref)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
