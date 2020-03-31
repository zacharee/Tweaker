package com.rw.tweaks.util

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.preference.PreferenceManager

class DemoController(context: Context) : ContextWrapper(context) {
    companion object {
        const val DEMO_PREFS = "demo_prefs"

        const val KEY_DEMO_ALLOWED = "sysui_demo_allowed"
        const val ACTION_DEMO = "com.android.systemui.demo"
        const val EXTRA_COMMAND = "command"

        const val COMMAND_ENTER = "enter"
        const val COMMAND_EXIT = "exit"
        const val COMMAND_STATUS = "status"
        const val COMMAND_NETWORK = "network"
    }

    val prefs = DemoPrefs(this)

    fun ensureDemoAllowed() {
        writeGlobal(KEY_DEMO_ALLOWED, true)
    }

    fun enterDemo() {
        ensureDemoAllowed()
        sendDemoCommand(COMMAND_ENTER)
    }

    fun exitDemo() {
        sendDemoCommand(COMMAND_EXIT)
    }

    fun sendDemoCommand(command: String, options: Bundle? = null) {
        val intent = Intent(ACTION_DEMO)
        intent.putExtra(EXTRA_COMMAND, command)
        if (options != null) intent.putExtras(options)

        sendBroadcast(intent)
    }

    class DemoPrefs(context: Context) : ContextWrapper(context), SharedPreferences {
        companion object {
            const val STATE_HIDE = "hide"

            const val VOLUME_STATE = "volume_state"
            const val BT_STATE = "bt_state"
            const val LOCATION_STATE = "location_state"
            const val ALARM_STATE = "alarm_state"
            const val SYNC_STATE = "sync_state"
            const val TTY_STATE = "tty_state"
            const val ERI_STATE = "eri_state"
            const val SECURE_STATE = "secure_state"
            const val MUTE_STATE = "mute_state"
            const val AIRPLANE_STATE = "airplane_state"
            const val WIFI_STATE = "wifi_state"
            const val MOBILE_STATE = "mobile_state"
        }

        private val wrapped = getSharedPreferences(DEMO_PREFS, Context.MODE_PRIVATE)

        val volumeState: String
            get() = getString(VOLUME_STATE, STATE_HIDE)
        val btState: String
            get() = getString(BT_STATE, STATE_HIDE)
        val locationState: String
            get() = getString(LOCATION_STATE, STATE_HIDE)
        val alarmState: String
            get() = getString(ALARM_STATE, STATE_HIDE)
        val syncState: String
            get() = getString(SYNC_STATE, STATE_HIDE)
        val ttyState: String
            get() = getString(TTY_STATE, STATE_HIDE)
        val eriState: String
            get() = getString(ERI_STATE, STATE_HIDE)
        val secureState: String
            get() = getString(SECURE_STATE, STATE_HIDE)
        val muteState: String
            get() = getString(MUTE_STATE, STATE_HIDE)
        val airplaneState: String
            get() = getString(AIRPLANE_STATE, STATE_HIDE)
        val wifiState: String
            get() = getString(WIFI_STATE, STATE_HIDE)
        val mobileState: String
            get() = getString(MOBILE_STATE, STATE_HIDE)

        override fun contains(key: String?): Boolean {
            return wrapped.contains(key)
        }

        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            return wrapped.getBoolean(key, defValue)
        }

        override fun getFloat(key: String?, defValue: Float): Float {
            return getFloat(key, defValue)
        }

        override fun getInt(key: String?, defValue: Int): Int {
            return wrapped.getInt(key, defValue)
        }

        override fun getLong(key: String?, defValue: Long): Long {
            return getLong(key, defValue)
        }

        override fun getString(key: String?, defValue: String?): String {
            return wrapped.getString(key, defValue)
        }

        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?
        ): MutableSet<String> {
            return getStringSet(key, defValues)
        }

        override fun getAll(): MutableMap<String, *> {
            return wrapped.all
        }

        override fun edit(): SharedPreferences.Editor {
            return wrapped.edit()
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            wrapped.registerOnSharedPreferenceChangeListener(listener)
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            wrapped.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}