package com.zacharee1.systemuituner.compose

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.zacharee1.systemuituner.util.prefManager

@Composable
fun <T> Context.rememberMonitorPreferenceState(
    key: String,
    value: () -> T,
): State<T> {
    return rememberPreferenceState(
        key = key,
        value = value,
        onChanged = {}
    )
}

@Composable
fun <T> Context.rememberPreferenceState(
    key: String,
    value: () -> T,
    onChanged: (T) -> Unit
): MutableState<T> {
    val state = remember(key) {
        mutableStateOf(value())
    }

    LaunchedEffect(key1 = state.value) {
        onChanged(state.value)
    }

    DisposableEffect(key1 = key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                state.value = value()
            }
        }

        prefManager.prefs.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            prefManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return state
}
