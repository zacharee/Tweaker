package com.zacharee1.systemuituner.compose.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.compose.components.SeekBar
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberFloatSettingsState
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.SettingsInfo

open class SeekBarPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    override val iconColor: Color? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    override val icon: Painter? = null,
    override val saveOption: Boolean = true,
    override val revertable: Boolean = false,
    val writeKey: Pair<SettingsType, String>,
    val minValue: Number,
    val maxValue: Number,
    val defaultValue: Number,
    val unit: String,
    val scale: Double = 1.0,
    val testing: Boolean = false,
) : SettingsPreferenceItem(
    title = title, key = key,
    minApi = minApi, maxApi = maxApi,
    enabled = enabled, iconColor = iconColor,
    dangerous = dangerous, summary = summary,
    icon = icon, saveOption = saveOption,
    revertable = revertable,
    dialogContents = {
        val context = LocalContext.current
        var state by if (testing) {
            remember {
                mutableStateOf(defaultValue)
            }
        } else {
            if (scale == 1.0) {
                context.rememberIntSettingsState(
                    key = writeKey,
                    saveOption = saveOption,
                    revertable = revertable,
                    def = defaultValue.toInt()
                )
            } else {
                context.rememberFloatSettingsState(
                    key = writeKey,
                    saveOption = saveOption,
                    revertable = revertable,
                    def = defaultValue.toFloat()
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            SeekBar(
                minValue = minValue,
                maxValue = maxValue,
                defaultValue = defaultValue,
                scale = scale,
                value = state,
                onValueChanged = { state = it },
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
open class SwitchPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    override val iconColor: Color? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    override val icon: Painter? = null,
    override val saveOption: Boolean = true,
    override val revertable: Boolean = false,
    val writeKeys: Array<Pair<SettingsType, String>>,
    val enabledValue: Any? = 1,
    val disabledValue: Any? = 0,
): SettingsPreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon, saveOption = saveOption,
    revertable = revertable, dialogContents = {
        val context = LocalContext.current
        var state by context.rememberBooleanSettingsState(
            keys = writeKeys,
            enabledValue = enabledValue,
            disabledValue = disabledValue,
            saveOption = saveOption,
            revertable = revertable,
        )

        OutlinedCard(
            onClick = { state = !state }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Switch(
                    checked = state,
                    onCheckedChange = { state = it }
                )
            }
        }
    }
)

open class SettingsPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    override val iconColor: Color? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    override val icon: Painter? = null,
    open val dialogContents: @Composable ColumnScope.(saveCallback: (Array<SettingsInfo>) -> Unit) -> Unit,
    open val saveOption: Boolean = true,
    open val revertable: Boolean = false,
) : BasePreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon
)

open class PreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    override val iconColor: Color? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    override val icon: Painter? = null,
    open val onClick: (() -> Unit)? = null,
) : BasePreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon
)

open class BasePreferenceItem(
    open val title: String,
    open val key: String,
    open val minApi: Int = -1,
    open val maxApi: Int = Int.MAX_VALUE,
    open val enabled: @Composable () -> Boolean = { true },
    open val iconColor: Color? = null,
    open val dangerous: Boolean = false,
    open val summary: String? = null,
    open val icon: Painter? = null,
)
