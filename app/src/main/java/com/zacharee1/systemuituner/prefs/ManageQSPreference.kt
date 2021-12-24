package com.zacharee1.systemuituner.prefs

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.DangerousPreference
import com.zacharee1.systemuituner.interfaces.IDangerousPreference
import com.zacharee1.systemuituner.util.isComponentEnabled
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure

open class ManageQSPreference(context: Context, attrs: AttributeSet?) : SwitchPreference(context, attrs),
    Preference.OnPreferenceChangeListener,
    IDangerousPreference by DangerousPreference(context, attrs) {

    private var manageComponent: ComponentName? = null

    init {
        isPersistent = false
        layoutResource = R.layout.custom_preference

        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ManageQSPreference, 0, 0)

            array.getString(R.styleable.ManageQSPreference_manage_component)?.let {
                manageComponent = ComponentName.unflattenFromString(it)
            }
        }
    }

    override fun onAttached() {
        super.onAttached()
        super.setOnPreferenceChangeListener(this)

        isChecked = manageComponent?.let {
            context.isComponentEnabled(it)
        } ?: false
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        //no-op
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val isChecked = newValue.toString().toBoolean()

        manageComponent?.let {
            context.packageManager.setComponentEnabledSetting(
                it,
                if (isChecked) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        return true
    }
}