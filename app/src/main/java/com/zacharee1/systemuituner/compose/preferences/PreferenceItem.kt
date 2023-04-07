package com.zacharee1.systemuituner.compose.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.SettingsInfo

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

        Switch(checked = state, onCheckedChange = { state = it })
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
