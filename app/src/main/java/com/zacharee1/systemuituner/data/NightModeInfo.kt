package com.zacharee1.systemuituner.data

data class NightModeInfo(
    var twilightMode: Int? = 0,
    var nightModeActivated: Int? = 0,
    var nightModeAuto: Int? = 0,
    var nightModeTemp: Int? = 0
) {
    fun nullAll() {
        twilightMode = null
        nightModeActivated = null
        nightModeAuto = null
        nightModeTemp = null
    }
}