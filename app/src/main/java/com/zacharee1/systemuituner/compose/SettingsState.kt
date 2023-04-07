package com.zacharee1.systemuituner.compose

import android.content.Context
import android.database.ContentObserver
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.SettingsInfo
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.writeSettingsBulk

@Composable
fun Context.rememberIntSettingsState(
    key: Pair<SettingsType, String>,
    saveOption: Boolean = true,
    revertable: Boolean = false,
    def: Int = 0
): MutableState<Number> {
    return rememberSettingsState(
        key = key,
        value = { getSetting(key.first, key.second)?.toIntOrNull() ?: def },
        saveOption = saveOption,
        revertable = revertable,
    )
}

@Composable
fun Context.rememberFloatSettingsState(
    key: Pair<SettingsType, String>,
    saveOption: Boolean = true,
    revertable: Boolean = false,
    def: Float = 0f
): MutableState<Number> {
    return rememberSettingsState(
        key = key,
        value = { getSetting(key.first, key.second)?.toFloatOrNull() ?: def },
        saveOption = saveOption,
        revertable = revertable,
    )
}

@Composable
fun Context.rememberBooleanSettingsState(
    keys: Array<Pair<SettingsType, String>>,
    enabledValue: Any? = 1,
    disabledValue: Any? = 0,
    saveOption: Boolean = true,
    revertable: Boolean = false,
): MutableState<Boolean> {
    val internalState = rememberSettingsState(
        keys = keys,
        value = {
            if (keys.all { (type, key) ->
                    getSetting(type, key) == enabledValue?.toString()
                }) enabledValue else disabledValue
        },
        saveOption = saveOption,
        revertable = revertable,
    )

    val pullUpState = remember(keys) {
        mutableStateOf(internalState.value == enabledValue)
    }

    LaunchedEffect(key1 = internalState.value) {
        pullUpState.value = internalState.value == enabledValue
    }

    LaunchedEffect(key1 = pullUpState.value) {
        internalState.value = if (pullUpState.value) enabledValue else disabledValue
    }

    return pullUpState
}

@Composable
fun <T : Any?> Context.rememberSettingsState(
    key: Pair<SettingsType, String>,
    value: () -> T,
    revertable: Boolean = false,
    saveOption: Boolean = true,
): MutableState<T> {
    return rememberSettingsState(
        keys = arrayOf(key),
        value = value,
        revertable = revertable,
        saveOption = saveOption,
    )
}

@Composable
fun <T : Any?> Context.rememberSettingsState(
    keys: Array<Pair<SettingsType, String>>,
    value: () -> T,
    revertable: Boolean = false,
    saveOption: Boolean = true,
): MutableState<T> {
    val state = remember(keys) {
        mutableStateOf(value())
    }

    LaunchedEffect(key1 = state.value) {
//        onChanged(state.value)

        if (!writeSettingsBulk(
                *keys.map { (type, key) ->
                    SettingsInfo(type, key, state.value)
                }.toTypedArray(),
                revertable = revertable,
                saveOption = saveOption,
            )
        ) {
            state.value = value()
        }
    }

    DisposableEffect(key1 = keys) {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                state.value = value()
            }
        }

        keys.forEach { (type, key) ->
            val uri = when (type) {
                SettingsType.GLOBAL -> Settings.Global.getUriFor(key)
                SettingsType.SECURE -> Settings.Secure.getUriFor(key)
                SettingsType.SYSTEM -> Settings.System.getUriFor(key)
                SettingsType.UNDEFINED -> null
            }

            if (uri != null) {
                contentResolver.registerContentObserver(uri, true, observer)
            }
        }

        onDispose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    return state
}
