package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.components.CardSwitch
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.data.SettingsType

@Composable
fun TetheringFixLayout() {
    val context = LocalContext.current

    var dunRequired by context.rememberBooleanSettingsState(
        keys = arrayOf(SettingsType.GLOBAL to Settings.Global.TETHER_DUN_REQUIRED),
        defaultValue = 1,
        enabledValue = 1,
        disabledValue = 0,
        saveOption = true,
    )

    var tetherSupported by context.rememberBooleanSettingsState(
        keys = arrayOf(SettingsType.GLOBAL to Settings.Global.TETHER_SUPPORTED),
        defaultValue = false,
        enabledValue = true,
        disabledValue = false,
        saveOption = true,
    )

    CardSwitch(
        title = stringResource(id = R.string.feature_fix_tethering),
        checked = !dunRequired && tetherSupported,
        onCheckedChange = {
            dunRequired = !it
            tetherSupported = it
        }
    )
}
