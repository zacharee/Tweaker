package com.zacharee1.systemuituner.compose.preferences

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.DemoModeActivity
import com.zacharee1.systemuituner.activities.IconBlacklistActivity
import com.zacharee1.systemuituner.activities.ManageQSActivity
import com.zacharee1.systemuituner.activities.QSEditorActivity
import com.zacharee1.systemuituner.compose.components.CardSwitch
import com.zacharee1.systemuituner.compose.components.ColorPickerWithText
import com.zacharee1.systemuituner.compose.preferences.layouts.AirplaneModeRadiosLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.AnimationScalesLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.CameraGesturesLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.ImmersiveModeLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.KeepOnPluggedLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.LockScreenShortcutsLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.NightModeLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.SMSLimitsLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.StorageThresholdLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.TetheringFixLayout
import com.zacharee1.systemuituner.compose.preferences.layouts.UISoundsLayout
import com.zacharee1.systemuituner.compose.rememberPreferenceState
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.isTouchWiz
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.verifiers.EnableStorage
import com.zacharee1.systemuituner.util.verifiers.ShowForFireOS
import com.zacharee1.systemuituner.views.NightModeView

val Context.allScreens by com.zacharee1.systemuituner.util.lazy {
    appsScreen.prefs +
            audioScreen.prefs +
            developerScreen.prefs +
            displayScreen.prefs +
            netCellularScreen.prefs +
            netWifiScreen.prefs +
            netMiscScreen.prefs +
            notificationsScreen.prefs +
            statusBarScreen.prefs +
            quickSettingsScreen.prefs +
            storageScreen.prefs +
            lockScreenScreen.prefs +
            uiScreen.prefs +
            advancedScreen.prefs
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

val Context.audioScreen by com.zacharee1.systemuituner.util.lazy {
    @Suppress("DEPRECATION")
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
                UISoundsLayout()
            },
            saveOption = true,
            revertable = true,
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.CAR_DOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.CAR_UNDOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.DESK_DOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.DESK_UNDOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.LOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.UNLOCK_SOUND,
                SettingsType.GLOBAL to Settings.Global.LOW_BATTERY_SOUND,
                SettingsType.GLOBAL to Settings.Global.TRUSTED_SOUND,
                SettingsType.GLOBAL to Settings.Global.WIRELESS_CHARGING_STARTED_SOUND,
                SettingsType.GLOBAL to Settings.Global.CHARGING_STARTED_SOUND,
                SettingsType.GLOBAL to Settings.Global.CHARGING_SOUNDS_ENABLED,
                SettingsType.SECURE to Settings.Secure.CHARGING_SOUNDS_ENABLED,
            ),
        ),
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
        SettingsPreferenceItem(
            title = resources.getString(R.string.option_touchwiz_navbar_color),
            summary = resources.getString(R.string.option_touchwiz_navbar_color_desc),
            icon = R.drawable.ic_baseline_color_lens_24,
            iconColor = R.color.pref_color_5,
            visible = { isTouchWiz },
            writeKeys = arrayOf(
                SettingsType.GLOBAL to "navigationbar_current_color",
                SettingsType.GLOBAL to "navigationbar_color",
            ),
            key = "navigation_bar_color",
            dialogContents = {
                val context = LocalContext.current
                var state by context.rememberSettingsState(
                    keys = arrayOf(
                        SettingsType.GLOBAL to "navigationbar_current_color",
                        SettingsType.GLOBAL to "navigationbar_color",
                    ),
                    value = {
                        getSetting(
                            SettingsType.GLOBAL,
                            "navigationbar_current_color",
                            getSetting(
                                SettingsType.GLOBAL,
                                "navigationbar_color",
                                Color.White.toArgb(),
                            )
                        )?.toIntOrNull() ?: Color.White.toArgb()
                    }
                )

                ColorPickerWithText(
                    color = Color(state),
                    defaultColor = Color.White,
                    onColorChanged = { state = it.toArgb() }
                )
            }
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_touchwiz_disable_high_brightness_warning),
            summary = resources.getString(R.string.option_touchwiz_disable_high_brightness_warning_desc),
            key = "shown_max_brightness_warning",
            writeKeys = arrayOf(SettingsType.SYSTEM to "shown_max_brightness_warning"),
            icon = R.drawable.ic_baseline_brightness_7_24,
            iconColor = R.color.pref_color_1,
            visible = { isTouchWiz }
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.option_night_mode),
            summary = resources.getString(R.string.option_night_mode_desc),
            icon = R.drawable.ic_baseline_nights_stay_24,
            iconColor = R.color.pref_color_4,
            key = "night_mode",
            writeKeys = arrayOf(
                SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_ACTIVATED,
                SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_AUTO_MODE,
                SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_COLOR_TEMPERATURE,
                SettingsType.SECURE to NightModeView.TWILIGHT_MODE,
            ),
            minApi = Build.VERSION_CODES.N,
            dialogContents = { NightModeLayout() },
        ),
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
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_sms_limit),
            summary = resources.getString(R.string.feature_sms_limit_desc),
            key = "sms_limit",
            icon = R.drawable.message_text_lock,
            iconColor = R.color.pref_color_6,
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT,
                SettingsType.GLOBAL to Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS,
            ),
            dialogContents = { SMSLimitsLayout() },
        ),
    ))
}

val Context.netWifiScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_fix_tethering),
            summary = resources.getString(R.string.feature_fix_tethering_desc),
            key = "tethering_fix",
            icon = R.drawable.link,
            iconColor = R.color.pref_color_1,
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.TETHER_DUN_REQUIRED,
                SettingsType.GLOBAL to Settings.Global.TETHER_SUPPORTED,
            ),
            dialogContents = { TetheringFixLayout() }
        ),
    ))
}

val Context.netMiscScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SettingsPreferenceItem(
            title = resources.getString(R.string.special_sub_airplane_mode),
            summary = resources.getString(R.string.special_sub_airplane_mode_desc),
            key = "airplane_mode_radios",
            icon = R.drawable.ic_baseline_airplanemode_active_24,
            iconColor = R.color.pref_color_4,
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.AIRPLANE_MODE_RADIOS,
                SettingsType.GLOBAL to Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS,
            ),
            dialogContents = { AirplaneModeRadiosLayout() },
        ),
    ))
}

val Context.notificationsScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_heads_up_notifications),
            summary = resources.getString(R.string.feature_heads_up_notifications_desc),
            key = Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED,
            icon = R.drawable.ic_baseline_notifications_active_24,
            iconColor = R.color.pref_color_7,
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED)
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.feature_heads_up_snooze_length),
            summary = resources.getString(R.string.feature_heads_up_snooze_length_desc),
            key = "heads_up_snooze_length_ms",
            defaultValue = 60000,
            icon = R.drawable.ic_baseline_notifications_paused_24,
            iconColor = R.color.pref_color_2,
            unit = "ms",
            writeKey = SettingsType.GLOBAL to "heads_up_snooze_length_ms",
            minValue = 0,
            maxValue = 600000,
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_dnd_in_volume),
            summary = resources.getString(R.string.feature_dnd_in_volume_desc),
            key = "sysui_show_full_zen",
            icon = R.drawable.do_not_disturb,
            iconColor = R.color.pref_color_1,
            writeKeys = arrayOf(SettingsType.SECURE to "sysui_show_full_zen"),
        ),
        // NOTIF SNOOZE TIMES PREF
    ))
}

val Context.statusBarScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        PreferenceItem(
            title = resources.getString(R.string.special_sub_icon_blacklist),
            summary = resources.getString(R.string.special_sub_icon_blacklist_desc),
            key = "icon_blacklist",
            icon = R.drawable.ic_baseline_visibility_off_24,
            iconColor = R.color.pref_color_1,
            onClick = {
                startActivity(Intent(this, IconBlacklistActivity::class.java))
            },
            persistable = true,
            writeKeys = arrayOf(SettingsType.SECURE to "icon_blacklist"),
        ),
        PreferenceItem(
            title = resources.getString(R.string.sub_demo),
            summary = resources.getString(R.string.sub_demo_desc),
            key = "sub_demo",
            icon = R.drawable.ic_baseline_tv_24,
            iconColor = R.color.pref_color_5,
            onClick = {
                startActivity(Intent(this, DemoModeActivity::class.java))
            },
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_clock_seconds),
            summary = resources.getString(R.string.feature_clock_seconds_desc),
            key = "clock_seconds",
            icon = R.drawable.ic_seconds,
            iconColor = R.color.pref_color_4,
            writeKeys = arrayOf(SettingsType.SECURE to "clock_seconds"),
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_battery_percent),
            summary = resources.getString(R.string.feature_battery_percent_desc),
            key = Settings.System.SHOW_BATTERY_PERCENT,
            icon = R.drawable.ic_percent,
            iconColor = R.color.pref_color_6,
            writeKeys = arrayOf(SettingsType.SYSTEM to Settings.System.SHOW_BATTERY_PERCENT),
        ),
        // CLOCK POSITION PREF
    ))
}

val Context.quickSettingsScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SeekBarPreferenceItem(
            title = resources.getString(R.string.option_qqs_tiles),
            summary = resources.getString(R.string.option_qqs_tiles_desc),
            key = "sysui_qqs_count",
            icon = R.drawable.ic_baseline_more_horiz_24,
            iconColor = R.color.pref_color_3,
            defaultValue = 5,
            writeKey = SettingsType.SECURE to "sysui_qqs_count",
            minValue = 1,
            maxValue = 12,
            minApi = Build.VERSION_CODES.N,
            maxApi = Build.VERSION_CODES.S,
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_qs_fancy_anim),
            summary = resources.getString(R.string.option_qs_fancy_anim_desc),
            key = "sysui_qs_fancy_anim",
            icon = R.drawable.animation,
            iconColor = R.color.pref_color_5,
            defaultValue = 1,
            writeKeys = arrayOf(SettingsType.SECURE to "sysui_qs_fancy_anim"),
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_qs_move_full_rows),
            summary = resources.getString(R.string.option_qs_move_full_rows_desc),
            key = "sysui_qs_move_whole_rows",
            icon = R.drawable.animation,
            iconColor = R.color.pref_color_7,
            defaultValue = 1,
            writeKeys = arrayOf(SettingsType.SECURE to "sysui_qs_move_whole_rows"),
        ),
        PreferenceItem(
            title = resources.getString(R.string.option_qs_editor),
            summary = resources.getString(R.string.option_qs_editor_desc),
            key = Settings.Secure.QS_TILES,
            icon = R.drawable.ic_baseline_view_grid_plus_24,
            iconColor = R.color.pref_color_2,
            writeKeys = arrayOf(SettingsType.SECURE to Settings.Secure.QS_TILES),
            persistable = true,
            onClick = {
                startActivity(Intent(this, QSEditorActivity::class.java))
            },
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_hide_multi_sim_panel),
            summary = resources.getString(R.string.option_hide_multi_sim_panel_desc),
            key = "multi_sim_bar_show_on_qspanel",
            icon = R.drawable.sim_off,
            iconColor = R.color.pref_color_6,
            writeKeys = arrayOf(SettingsType.SECURE to "multi_sim_bar_show_on_qspanel"),
            minApi = Build.VERSION_CODES.P,
            enabledValue = 0,
            disabledValue = 1,
            visible = { isTouchWiz },
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.option_touchwiz_qs_row_count),
            summary = resources.getString(R.string.option_touchwiz_qs_row_count_desc),
            key = "qs_tile_row",
            icon = R.drawable.ic_baseline_more_vert_24,
            iconColor = R.color.pref_color_1,
            writeKey = SettingsType.SECURE to "qs_tile_row",
            maxApi = Build.VERSION_CODES.O_MR1,
            defaultValue = 1,
            minValue = 1,
            maxValue = 10,
            visible = { isTouchWiz },
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.option_touchwiz_qs_column_count),
            summary = resources.getString(R.string.option_touchwiz_qs_column_count_desc),
            key = "qs_tile_column",
            icon = R.drawable.ic_baseline_more_horiz_24,
            iconColor = R.color.pref_color_4,
            writeKey = SettingsType.SECURE to "qs_tile_column",
            maxApi = Build.VERSION_CODES.O_MR1,
            defaultValue = 1,
            minValue = 1,
            maxValue = 10,
            visible = { isTouchWiz },
        ),
    ))
}

val Context.storageScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_insufficient_storage_warning),
            summary = resources.getString(R.string.feature_insufficient_storage_warning_desc),
            key = "storage_threshold",
            icon = R.drawable.ic_baseline_disc_full_24,
            iconColor = R.color.pref_color_4,
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE,
                SettingsType.GLOBAL to Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES,
            ),
            dialogContents = { StorageThresholdLayout() },
        )
    ))
}

val Context.lockScreenScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_lockscreen_shortcuts),
            summary = resources.getString(R.string.feature_lockscreen_shortcuts_desc),
            key = "lock_screen_shortcuts",
            writeKeys = arrayOf(
                SettingsType.SECURE to "sysui_keyguard_left",
                SettingsType.SECURE to "sysui_keyguard_right",
                SettingsType.SYSTEM to "lock_application_shortcut",
            ),
            icon = R.drawable.lock_open,
            iconColor = R.color.pref_color_3,
            minApi = Build.VERSION_CODES.O,
            dialogContents = { LockScreenShortcutsLayout() },
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_allow_custom_left_lock_shortcut),
            summary = resources.getString(R.string.option_allow_custom_left_lock_shortcut_desc),
            key = "SG_EN",
            defaultValue = 1,
            icon = R.drawable.lock_open,
            iconColor = R.color.pref_color_2,
            enabledValue = 0,
            disabledValue = 1,
            writeKeys = arrayOf(SettingsType.GLOBAL to "SG_EN"),
            visible = { ShowForFireOS(this).shouldShow },
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_disable_lock_screen_ads),
            summary = resources.getString(R.string.option_disable_lock_screen_ads_desc),
            key = "LOCKSCREEN_AD_ENABLED",
            defaultValue = 1,
            icon = R.drawable.no_ads,
            iconColor = R.color.pref_color_4,
            enabledValue = 0,
            disabledValue = 1,
            writeKeys = arrayOf(SettingsType.GLOBAL to "LOCKSCREEN_AD_ENABLED"),
            visible = { ShowForFireOS(this).shouldShow },
        ),
    ))
}

val Context.uiScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        SeekBarPreferenceItem(
            title = resources.getString(R.string.feature_long_press_delay),
            summary = resources.getString(R.string.feature_long_press_delay_desc),
            key = Settings.Secure.LONG_PRESS_TIMEOUT,
            defaultValue = 400,
            icon = R.drawable.tap_hold,
            iconColor = R.color.pref_color_3,
            unit = "ms",
            minValue = 100,
            maxValue = 5000,
            writeKey = SettingsType.SECURE to Settings.Secure.LONG_PRESS_TIMEOUT,
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.option_disable_combined_internet),
            summary = resources.getString(R.string.option_disable_combined_internet_desc),
            key = "settings_provider_model",
            icon = R.drawable.ic_network,
            writeKeys = arrayOf(SettingsType.GLOBAL to "settings_provider_model"),
            enabledValue = 0,
            disabledValue = 1,
            iconColor = R.color.pref_color_1,
            minApi = Build.VERSION_CODES.S,
            maxApi = Build.VERSION_CODES.S_V2,
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.option_back_gesture_inset_scale_left),
            summary = resources.getString(R.string.option_back_gesture_inset_scale_left_desc),
            key = Settings.Secure.BACK_GESTURE_INSET_SCALE_LEFT,
            defaultValue = 1,
            icon = R.drawable.border_left_variant,
            iconColor = R.color.pref_color_5,
            scale = 0.01,
            minValue = 0,
            maxValue = 5,
            writeKey = SettingsType.SECURE to Settings.Secure.BACK_GESTURE_INSET_SCALE_LEFT,
            minApi = Build.VERSION_CODES.R,
        ),
        SeekBarPreferenceItem(
            title = resources.getString(R.string.option_back_gesture_inset_scale_right),
            summary = resources.getString(R.string.option_back_gesture_inset_scale_right_desc),
            key = Settings.Secure.BACK_GESTURE_INSET_SCALE_RIGHT,
            defaultValue = 1,
            icon = R.drawable.border_right_variant,
            iconColor = R.color.pref_color_6,
            scale = 0.01,
            minValue = 0,
            maxValue = 5,
            writeKey = SettingsType.SECURE to Settings.Secure.BACK_GESTURE_INSET_SCALE_RIGHT,
            minApi = Build.VERSION_CODES.R,
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_keep_screen_on),
            summary = resources.getString(R.string.feature_keep_screen_on_desc),
            icon = R.drawable.ic_baseline_visibility_24,
            iconColor = R.color.pref_color_2,
            dialogContents = { KeepOnPluggedLayout() },
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.STAY_ON_WHILE_PLUGGED_IN),
            key = Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_custom_animation_scales),
            summary = resources.getString(R.string.feature_custom_animation_scales_desc),
            icon = R.drawable.animation,
            iconColor = R.color.pref_color_7,
            key = "animation_scales",
            writeKeys = arrayOf(
                SettingsType.GLOBAL to Settings.Global.ANIMATOR_DURATION_SCALE,
                SettingsType.GLOBAL to Settings.Global.TRANSITION_ANIMATION_SCALE,
                SettingsType.GLOBAL to Settings.Global.WINDOW_ANIMATION_SCALE,
            ),
            dialogContents = { AnimationScalesLayout() }
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_camera_gestures),
            summary = resources.getString(R.string.feature_camera_gestures_desc),
            icon = R.drawable.ic_baseline_camera_24,
            iconColor = R.color.pref_color_6,
            key = "camera_gestures",
            writeKeys = arrayOf(
                SettingsType.SECURE to Settings.Secure.CAMERA_GESTURE_DISABLED,
                SettingsType.SECURE to Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                SettingsType.SECURE to Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED,
            ),
            dialogContents = { CameraGesturesLayout() }
        ),
        SettingsPreferenceItem(
            title = resources.getString(R.string.feature_immersive_mode),
            summary = resources.getString(R.string.feature_immersive_mode_desc),
            key = "immersive_mode",
            writeKeys = arrayOf(SettingsType.GLOBAL to Settings.Global.POLICY_CONTROL),
            icon = R.drawable.ic_baseline_fullscreen_24,
            iconColor = R.color.pref_color_4,
            dialogContents = { ImmersiveModeLayout() }
        ),
        SwitchPreferenceItem(
            title = resources.getString(R.string.feature_dark_mode),
            summary = resources.getString(R.string.feature_dark_mode_desc),
            icon = R.drawable.light_dark,
            iconColor = R.color.pref_color_5,
            key = Settings.Secure.UI_NIGHT_MODE,
            writeKeys = arrayOf(
                SettingsType.SECURE to Settings.Secure.UI_NIGHT_MODE,
            ),
            enabledValue = UiModeManager.MODE_NIGHT_YES,
            disabledValue = UiModeManager.MODE_NIGHT_NO,
            defaultValue = UiModeManager.MODE_NIGHT_NO,
        ),
    ))
}

val Context.advancedScreen by com.zacharee1.systemuituner.util.lazy {
    Screen(listOf(
        // READ SETTING
        // WRITE SETTING
        SettingsPreferenceItem(
            title = resources.getString(R.string.option_advanced_force_enable_all),
            summary = resources.getString(R.string.option_advanced_force_enable_all_desc),
            key = "force_enable_all",
            persistable = false,
            writeKeys = arrayOf(),
            icon = R.drawable.ic_baseline_toggle_on_24,
            iconColor = R.color.pref_color_4,
            dialogContents = {
                val context = LocalContext.current
                var state by context.rememberPreferenceState(
                    key = PrefManager.FORCE_ENABLE_ALL,
                    value = { context.prefManager.forceEnableAll },
                    onChanged = { context.prefManager.forceEnableAll = it }
                )

                CardSwitch(
                    title = resources.getString(R.string.option_advanced_force_enable_all),
                    checked = state,
                    onCheckedChange = { state = it },
                )
            },
            dangerous = true,
        ),
        PreferenceItem(
            title = resources.getString(R.string.option_advanced_manage_qs_tiles),
            summary = resources.getString(R.string.option_advanced_manage_qs_tiles_desc),
            icon = R.drawable.ic_baseline_toggle_on_24,
            iconColor = R.color.pref_color_6,
            minApi = Build.VERSION_CODES.N,
            onClick = {
                startActivity(Intent(this, ManageQSActivity::class.java))
            },
            key = "tile_settings",
        ),
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
