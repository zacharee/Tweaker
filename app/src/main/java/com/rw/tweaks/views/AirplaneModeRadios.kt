package com.rw.tweaks.views

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rw.tweaks.util.writeGlobal
import kotlinx.android.synthetic.main.airplane_mode_radios.view.*

class AirplaneModeRadios(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    companion object {
        const val CELL = "cell"
        const val BT = "bluetooth"
        const val WIFI = "wifi"
        const val NFC = "nfc"
        const val WMX = "wimax"
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val currentBlacklisted = (Settings.Global.getString(context.contentResolver, Settings.Global.AIRPLANE_MODE_RADIOS) ?: "").split(",")
        val currentToggleable = (Settings.Global.getString(context.contentResolver, Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS) ?: "").split(",")
        
        val radioCell = radio_cell
        val radioBt = radio_bt
        val radioWifi = radio_wifi
        val radioNfc = radio_nfc
        val radioWmx = radio_wimax
        
        val toggleCell = toggle_cell
        val toggleBt = toggle_bluetooth
        val toggleWifi = toggle_wifi
        val toggleNfc = toggle_nfc
        val toggleWmx = toggle_wimax

        radioCell.apply { 
            isChecked = !currentBlacklisted.contains(CELL)
            setOnCheckedChangeListener { _, isChecked -> 
                updateBlacklist(isChecked, radioBt.isChecked, radioWifi.isChecked, radioNfc.isChecked, radioWmx.isChecked)
            }
        }

        radioBt.apply {
            isChecked = !currentBlacklisted.contains(BT)
            setOnCheckedChangeListener { _, isChecked ->
                updateBlacklist(radioCell.isChecked, isChecked, radioWifi.isChecked, radioNfc.isChecked, radioWmx.isChecked)
            }
        }

        radioWifi.apply {
            isChecked = !currentBlacklisted.contains(WIFI)
            setOnCheckedChangeListener { _, isChecked ->
                updateBlacklist(radioCell.isChecked, radioBt.isChecked, isChecked, radioNfc.isChecked, radioWmx.isChecked)
            }
        }

        radioNfc.apply {
            isChecked = !currentBlacklisted.contains(NFC)
            setOnCheckedChangeListener { _, isChecked ->
                updateBlacklist(radioCell.isChecked, radioBt.isChecked, radioWifi.isChecked, isChecked, radioWmx.isChecked)
            }
        }

        radioWmx.apply {
            isChecked = !currentBlacklisted.contains(WMX)
            setOnCheckedChangeListener { _, isChecked ->
                updateBlacklist(radioCell.isChecked, radioBt.isChecked, radioWifi.isChecked, radioNfc.isChecked, isChecked)
            }
        }

        toggleCell.apply {
            isChecked = currentToggleable.contains(CELL)
            setOnCheckedChangeListener { _, isChecked ->
                updateToggleable(isChecked, toggleBt.isChecked, toggleWifi.isChecked, toggleNfc.isChecked, toggleWmx.isChecked)
            }
        }

        toggleBt.apply {
            isChecked = currentToggleable.contains(BT)
            setOnCheckedChangeListener { _, isChecked ->
                updateToggleable(toggleCell.isChecked, isChecked, toggleWifi.isChecked, toggleNfc.isChecked, toggleWmx.isChecked)
            }
        }

        toggleWifi.apply {
            isChecked = currentToggleable.contains(WIFI)
            setOnCheckedChangeListener { _, isChecked ->
                updateToggleable(toggleCell.isChecked, toggleBt.isChecked, isChecked, toggleNfc.isChecked, toggleWmx.isChecked)
            }
        }

        toggleNfc.apply {
            isChecked = currentToggleable.contains(NFC)
            setOnCheckedChangeListener { _, isChecked ->
                updateToggleable(toggleCell.isChecked, toggleBt.isChecked, toggleWifi.isChecked, isChecked, toggleWmx.isChecked)
            }
        }

        toggleWmx.apply {
            isChecked = currentToggleable.contains(WMX)
            setOnCheckedChangeListener { _, isChecked ->
                updateToggleable(toggleCell.isChecked, toggleBt.isChecked, toggleWifi.isChecked, toggleNfc.isChecked, isChecked)
            }
        }
    }
    
    private fun updateBlacklist(isCellChecked: Boolean, isBtChecked: Boolean, isWifiChecked: Boolean, isNfcChecked: Boolean, isWmxChecked: Boolean) {
        val list = arrayListOf<String>()
        if (!isCellChecked) list.add(CELL)
        if (!isBtChecked) list.add(BT)
        if (!isWifiChecked) list.add(WIFI)
        if (!isNfcChecked) list.add(NFC)
        if (!isWmxChecked) list.add(WMX)
        
        val string = TextUtils.join(",", list)
        
        context.writeGlobal(Settings.Global.AIRPLANE_MODE_RADIOS, string)
    }
    
    private fun updateToggleable(isCellChecked: Boolean, isBtChecked: Boolean, isWifiChecked: Boolean, isNfcChecked: Boolean, isWmxChecked: Boolean) {
        val list = arrayListOf<String>()
        if (isCellChecked) list.add(CELL)
        if (isBtChecked) list.add(BT)
        if (isWifiChecked) list.add(WIFI)
        if (isNfcChecked) list.add(NFC)
        if (isWmxChecked) list.add(WMX)

        val string = TextUtils.join(",", list)

        context.writeGlobal(Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS, string)
    }
}