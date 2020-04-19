package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeGlobal
import kotlinx.android.synthetic.main.airplane_mode_radio.view.*
import kotlinx.android.synthetic.main.airplane_mode_radios.view.*

class AirplaneModeRadios(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    companion object {
        const val CELL = "cell"
        const val BT = "bluetooth"
        const val WIFI = "wifi"
        const val NFC = "nfc"
        const val WMX = "wimax"
    }

    data class RadioInfo(
        val name: Int,
        val id: String,
        var isExempt: Boolean,
        var isToggleable: Boolean
    )
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val currentBlacklisted = (Settings.Global.getString(context.contentResolver, Settings.Global.AIRPLANE_MODE_RADIOS) ?: "").split(",")
        val currentToggleable = (Settings.Global.getString(context.contentResolver, Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS) ?: "").split(",")

        val items = arrayListOf(
            RadioInfo(
                R.string.option_airplane_mode_radio_cell,
                CELL,
                !currentBlacklisted.contains(CELL),
                currentToggleable.contains(CELL)
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_bluetooth,
                BT,
                !currentBlacklisted.contains(BT),
                currentToggleable.contains(BT)
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_wifi,
                WIFI,
                !currentBlacklisted.contains(WIFI),
                currentToggleable.contains(WIFI)
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_nfc,
                NFC,
                !currentBlacklisted.contains(NFC),
                currentToggleable.contains(NFC)
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_wimax,
                WMX,
                !currentBlacklisted.contains(WMX),
                currentToggleable.contains(WMX)
            )
        )

        val adapter = RadioAdapter(items)
        radio_recycler.adapter = adapter
    }

    class RadioAdapter(private val items: ArrayList<RadioInfo>) : RecyclerView.Adapter<RadioAdapter.VH>() {
        class VH(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.airplane_mode_radio, parent, false))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                val info = items[position]

                radio.text = resources.getText(info.name)
                exempt.isChecked = info.isExempt
                toggleable.isChecked = info.isToggleable

                exempt.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]
                    exempt.isChecked = !exempt.isChecked
                    newInfo.isExempt = exempt.isChecked
                    update(context)
                }
                toggleable.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]
                    toggleable.isChecked = !toggleable.isChecked
                    newInfo.isToggleable = toggleable.isChecked
                    update(context)
                }
            }
        }

        private fun update(context: Context) {
            val blacklisted = ArrayList<String>()
            val toggleable = ArrayList<String>()

            items.forEach {
                if (!it.isExempt) blacklisted.add(it.id)
                if (it.isToggleable) toggleable.add(it.id)
            }

            val blString = blacklisted.joinToString(",")
            val toggleString = toggleable.joinToString(",")

            context.prefManager.putString(Settings.Global.AIRPLANE_MODE_RADIOS, blString)
            context.writeGlobal(Settings.Global.AIRPLANE_MODE_RADIOS, blString)
            context.prefManager.putString(Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS, toggleString)
            context.writeGlobal(Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS, toggleString)
        }
    }
}