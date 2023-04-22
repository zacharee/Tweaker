package com.zacharee1.systemuituner.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.compose.preferences.BasePreference
import com.zacharee1.systemuituner.compose.preferences.BaseSettingsPreference
import com.zacharee1.systemuituner.compose.preferences.PreferenceItem
import com.zacharee1.systemuituner.compose.preferences.SettingsPreferenceItem
import com.zacharee1.systemuituner.compose.preferences.allScreens
import com.zacharee1.systemuituner.compose.rememberMonitorPreferenceState
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefManager

class ComposeTest : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val forceEnableAll by context.rememberMonitorPreferenceState(
                key = PrefManager.FORCE_ENABLE_ALL,
                value = { context.prefManager.forceEnableAll }
            )
            val items = context.allScreens.filter {
                forceEnableAll || it.visible()
            }

            Mdc3Theme {
                Surface {
                    LazyColumn(
                        modifier = Modifier,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items,
                            key = { it.key }
                        ) { pref ->
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
