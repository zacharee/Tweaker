package com.zacharee1.systemuituner.dialogs

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.specific.AdbWifiPreference
import kotlinx.coroutines.launch

class SwitchOptionDialog : BaseOptionDialog() {
    companion object {
        const val ARG_DISABLED = "disabled"
        const val ARG_ENABLED = "enabled"
        const val ARG_CHECKED = "checked"

        fun newInstance(key: String, disabled: String?, enabled: String?, shouldBeChecked: Boolean? = null): SwitchOptionDialog {
            return SwitchOptionDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                    putString(ARG_DISABLED, disabled)
                    putString(ARG_ENABLED, enabled)
                    putString(ARG_CHECKED, shouldBeChecked?.toString())
                }
            }
        }
    }

    override val layoutRes = R.layout.secure_switch

    private val disabled by lazy { arguments?.getString(ARG_DISABLED) ?: "0" }
    private val enabled by lazy { arguments?.getString(ARG_ENABLED) ?: "1" }
    private val shouldBeChecked by lazy { arguments?.getString(ARG_CHECKED)?.toBoolean() ?: false }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        view.findViewById<MaterialSwitch>(R.id.secure_switch).apply {
            text = preference.title
            isChecked = shouldBeChecked

            val adbWifiPref = preference as? AdbWifiPreference
            if (adbWifiPref != null) {
                fun updateText(port: Int?) {
                    text = if (port != null) {
                        context.getString(
                            R.string.feature_enable_wireless_adb_title_with_port,
                            preference.title,
                            port
                        )
                    } else {
                        preference.title
                    }
                }
                updateText(adbWifiPref.discoveredPort)
                adbWifiPref.onPortDiscoveredListener = { port ->
                    post { updateText(port) }
                }
            }

            setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    val thisRef = this

                    launch {
                        val newValue = if (isChecked) enabled else disabled
                        if (!notifyChanged(newValue)) {
                            this@apply.setOnCheckedChangeListener(null)
                            this@apply.isChecked = !isChecked
                            this@apply.setOnCheckedChangeListener(thisRef)
                        }
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        (preference as? AdbWifiPreference)?.onPortDiscoveredListener = null
        super.onDestroyView()
    }
}
