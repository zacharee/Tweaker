package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings

class ImmersiveManager(context: Context) : ContextWrapper(context) {
    enum class ImmersiveMode(val type: String) {
        NONE("immersive.none"),
        STATUS("immersive.status"),
        NAV("immersive.navigation"),
        FULL("immersive.full")
    }

    data class ImmersiveInfo(
        var allFull: Boolean = false,
        var allStatus: Boolean = false,
        var allNav: Boolean = false,
        val fullApps: ArrayList<String> = ArrayList(),
        val fullBl: ArrayList<String> = ArrayList(),
        val statusApps: ArrayList<String> = ArrayList(),
        val statusBl: ArrayList<String> = ArrayList(),
        val navApps: ArrayList<String> = ArrayList(),
        val navBl: ArrayList<String> = ArrayList()
    ) {
        fun clear() {
            allFull = false
            allStatus = false
            allNav = false
            fullApps.clear()
            statusApps.clear()
            navApps.clear()
            fullBl.clear()
            statusBl.clear()
            navBl.clear()
        }
    }

    fun setAdvancedImmersive(info: ImmersiveInfo) {
        val modes = ArrayList<String?>()

        val fullMode = buildModeString(ImmersiveMode.FULL.type, info.allFull, info.fullApps, info.fullBl)
        val statusMode = buildModeString(ImmersiveMode.STATUS.type, info.allStatus, info.statusApps, info.statusBl)
        val navMode = buildModeString(ImmersiveMode.NAV.type, info.allNav, info.navApps, info.navBl)

        if (fullMode.isNotBlank()) modes.add(fullMode)
        if (statusMode.isNotBlank()) modes.add(statusMode)
        if (navMode.isNotBlank()) modes.add(navMode)

        val string = if (modes.isEmpty()) ImmersiveMode.NONE.type else modes.joinToString(separator = ":")

        prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.POLICY_CONTROL, string)
        writeGlobal(Settings.Global.POLICY_CONTROL, string)
    }

    fun parseAdvancedImmersive(value: String? = getSetting(SettingsType.GLOBAL, Settings.Global.POLICY_CONTROL)): ImmersiveInfo {
        val info = ImmersiveInfo()

        if (value.isNullOrBlank() || value == ImmersiveMode.NONE.type) {
            return info
        }

        val split = value.split(":")

        split.forEach {
            val typeAndValues = it.split("=")
            if (typeAndValues.size <= 1) return info

            val type = typeAndValues[0]
            val values by lazy { typeAndValues[1].split(",") }

            if (values[0] == "*") {
                when (type) {
                    ImmersiveMode.FULL.type -> info.allFull = true
                    ImmersiveMode.STATUS.type -> info.allStatus = true
                    ImmersiveMode.NAV.type -> info.allNav = true
                }
            }

            values.forEach { value ->
                if (value != "*") {
                    val isBl = value.startsWith("-")
                    when (type) {
                        ImmersiveMode.FULL.type -> if (isBl) info.fullBl.add(value.removePrefix("-")) else info.fullApps.add(value)
                        ImmersiveMode.STATUS.type -> if (isBl) info.statusBl.add(value.removePrefix("-")) else info.statusApps.add(value)
                        ImmersiveMode.NAV.type -> if (isBl) info.navBl.add(value.removePrefix("-")) else info.navApps.add(value)
                    }
                }
            }
        }

        return info
    }

    private fun buildModeString(type: String, all: Boolean, apps: ArrayList<String>, bl: ArrayList<String>): String {
        val builder = StringBuilder()

        if (all) builder.append("$type=*")
        else if (apps.isNotEmpty()) builder.append("$type=${apps.joinToString(separator = ",")}")

        if (bl.isNotEmpty()) builder.append(bl.joinToString(separator = ",-", prefix = if (apps.isEmpty()) "$type=-" else ",-"))

        return builder.toString()
    }
}