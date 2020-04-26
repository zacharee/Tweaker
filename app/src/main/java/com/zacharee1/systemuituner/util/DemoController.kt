package com.zacharee1.systemuituner.util

import android.content.*
import android.os.Bundle

class DemoController private constructor(context: Context) : ContextWrapper(context) {
    object Keys {
        const val KEY_VOLUME = "volume"
        const val KEY_BLUETOOTH = "bluetooth"
        const val KEY_LOCATION = "location"
        const val KEY_ALARM = "alarm"
        const val KEY_SYNC = "sync"
        const val KEY_TTY = "tty"
        const val KEY_ERI = "eri"
        const val KEY_SECURE = "secure"
        const val KEY_MUTE = "mute"
        const val KEY_SPEAKERPHONE = "speakerphone"
        const val KEY_AIRPLANE = "airplane"
        const val KEY_WIFI = "wifi"
        const val KEY_MOBILE = "mobile"

        const val KEY_LEVEL = "level"
        const val KEY_FULLY = "fully"
        const val KEY_DATATYPE = "datatype"
        const val KEY_PLUGGED = "plugged"
        const val KEY_HHMM = "hhmm"
        const val KEY_MODE = "mode"
    }

    companion object {
        const val DEMO_PREFS = "demo_prefs"

        const val KEY_DEMO_ALLOWED = "sysui_demo_allowed"
        const val ACTION_DEMO = "com.android.systemui.demo"
        const val EXTRA_COMMAND = "command"

        const val COMMAND_ENTER = "enter"
        const val COMMAND_EXIT = "exit"
        const val COMMAND_STATUS = "status"
        const val COMMAND_NETWORK = "network"
        const val COMMAND_CLOCK = "clock"
        const val COMMAND_BATTERY = "battery"
        const val COMMAND_BARS = "bars"

        private var instance: DemoController? = null

        fun getInstance(context: Context): DemoController {
            return instance ?: run {
                instance = DemoController(context.applicationContext)
                instance!!
            }
        }
    }

    var isCurrentlyEnabled = false

    val prefs = DemoPrefs(this)
    val updateListeners = HashSet<(enabled: Boolean) -> Unit>()

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DEMO) {
                val mode = intent.getStringExtra(EXTRA_COMMAND)

                isCurrentlyEnabled = mode != COMMAND_EXIT
                updateListeners.forEach { it(isCurrentlyEnabled) }
            }
        }
    }

    init {
        registerReceiver(stateReceiver, IntentFilter(ACTION_DEMO))
    }

    fun ensureDemoAllowed() {
        writeGlobal(KEY_DEMO_ALLOWED, 1)
    }

    fun enterDemo() {
        ensureDemoAllowed()
        sendDemoCommand(COMMAND_ENTER)
        updateAll()
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

    fun updateAll() {
        updateStatusState()
        updateAirplaneState()
        updateWiFiState()
        updateMobileState()
        updateBatteryState()
        updateClockState()
        updateBarState()
    }

    fun updateStatusState() {
        sendDemoCommand(COMMAND_STATUS, Bundle().apply {
            putString(Keys.KEY_VOLUME, prefs.volumeState)
            putString(Keys.KEY_BLUETOOTH, prefs.btState)
            putString(Keys.KEY_LOCATION, prefs.locationState)
            putString(Keys.KEY_ALARM, prefs.alarmState)
            putString(Keys.KEY_SYNC, prefs.syncState)
            putString(Keys.KEY_TTY, prefs.ttyState)
            putString(Keys.KEY_ERI, prefs.eriState)
            putString(Keys.KEY_SECURE, prefs.secureState)
            putString(Keys.KEY_MUTE, prefs.muteState)
            putString(Keys.KEY_SPEAKERPHONE, prefs.speakerphoneState)
        })
    }

    fun updateAirplaneState() {
        sendDemoCommand(COMMAND_NETWORK, Bundle().apply {
            putString(Keys.KEY_AIRPLANE, prefs.airplaneState)
        })
    }

    fun updateWiFiState() {
        sendDemoCommand(COMMAND_NETWORK, Bundle().apply {
            putString(Keys.KEY_WIFI, prefs.wifiState)
            putString(Keys.KEY_LEVEL, prefs.wifiLevel.toString())
            putString(Keys.KEY_FULLY, prefs.wifiFully.toString())
        })
    }

    fun updateMobileState() {
        sendDemoCommand(COMMAND_NETWORK, Bundle().apply {
            putString(Keys.KEY_MOBILE, prefs.mobileState)
            putString(Keys.KEY_LEVEL, prefs.mobileLevel.toString())
            putString(Keys.KEY_FULLY, prefs.mobileFully.toString())
            putString(Keys.KEY_DATATYPE, prefs.mobileType)
        })
    }

    fun updateBatteryState() {
        sendDemoCommand(COMMAND_BATTERY, Bundle().apply {
            putString(Keys.KEY_LEVEL, prefs.batteryLevel.toString())
            putString(Keys.KEY_PLUGGED, prefs.batteryPlugged.toString())
        })
    }

    fun updateClockState() {
        sendDemoCommand(COMMAND_CLOCK, Bundle().apply {
            putString(Keys.KEY_HHMM, prefs.clockTime)
        })
    }

    fun updateBarState() {
        sendDemoCommand(COMMAND_BARS, Bundle().apply {
            putString(Keys.KEY_MODE, prefs.barMode)
        })
    }

    class DemoPrefs(context: Context) : ContextWrapper(context), SharedPreferences {
        object MobileTypes {
            const val TYPE_1X = "1x"
            const val TYPE_3G = "3g"
            const val TYPE_4G = "4g"
            const val TYPE_E = "e"
            const val TYPE_G = "g"
            const val TYPE_H = "h"
            const val TYPE_LTE = "lte"
            const val TYPE_ROAM = "roam"
            const val TYPE_NONE = ""
        }

        object BarModes {
            const val MODE_OPAQUE = "opaque"
            const val MODE_SEMI_TRANSPARENT = "semi-transparent"
            const val MODE_TRANSLUCENT = "translucent"
        }

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
            const val SPEAKERPHONE_STATE = "speakerphone_state"

            const val WIFI_LEVEL = "wifi_level"
            const val MOBILE_LEVEL = "mobile_level"
            const val BATTERY_LEVEL = "battery_level"

            const val WIFI_FULLY = "wifi_fully"
            const val MOBILE_FULLY = "mobile_fully"
            const val BATTERY_PLUGGED = "battery_plugged"

            const val MOBILE_TYPE = "mobile_type"
            const val CLOCK_TIME = "clock_time"
            const val BAR_MODE = "bar_mode"
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
        val speakerphoneState: String
            get() = getString(SPEAKERPHONE_STATE, STATE_HIDE)
        val mobileType: String
            get() = getString(MOBILE_TYPE, MobileTypes.TYPE_NONE)
        val clockTime: String
            get() = getString(CLOCK_TIME, "1200")
        val barMode: String
            get() = getString(BAR_MODE, BarModes.MODE_OPAQUE)

        val wifiLevel: Int
            get() = getFloat(WIFI_LEVEL, 0f).toInt()
        val mobileLevel: Int
            get() = getFloat(MOBILE_LEVEL, 0f).toInt()
        val batteryLevel: Int
            get() = getFloat(BATTERY_LEVEL, 100f).toInt()

        val wifiFully: Boolean
            get() = getString(WIFI_FULLY, "false").toBoolean()
        val mobileFully: Boolean
            get() = getString(MOBILE_FULLY, "false").toBoolean()
        val batteryPlugged: Boolean
            get() = getString(BATTERY_PLUGGED, "false").toBoolean()

        override fun contains(key: String?): Boolean {
            return wrapped.contains(key)
        }

        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            return wrapped.getBoolean(key, defValue)
        }

        override fun getFloat(key: String?, defValue: Float): Float {
            return wrapped.getFloat(key, defValue)
        }

        override fun getInt(key: String?, defValue: Int): Int {
            return wrapped.getInt(key, defValue)
        }

        override fun getLong(key: String?, defValue: Long): Long {
            return wrapped.getLong(key, defValue)
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