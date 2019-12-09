package com.rw.tweaks.prefs.secure.base

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.verifiers.BasePreferenceEnabledVerifier
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier
import kotlinx.android.synthetic.main.custom_preference.view.*

open class BaseSecurePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    override var writeKey: String? = null
        get() = field ?: key

    init {
        layoutResource = R.layout.custom_preference

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.BaseSecurePreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.BaseSecurePreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.BaseSecurePreference_differing_key)
        dangerous = array.getBoolean(R.styleable.BaseSecurePreference_dangerous, false)
        lowApi = array.getInt(R.styleable.BaseSecurePreference_low_api, lowApi)
        highApi = array.getInt(R.styleable.BaseSecurePreference_high_api, highApi)
        iconColor = array.getColor(R.styleable.BaseSecurePreference_icon_color, iconColor)

        val clazz = array.getString(R.styleable.BaseSecurePreference_visibility_verifier)
        if (clazz != null) {
            visibilityVerifier = context.classLoader.loadClass(clazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseVisibilityVerifier
        }

        val enabledClazz = array.getString(R.styleable.BaseSecurePreference_enabled_verifier)
        if (enabledClazz != null) {
            enabledVerifier = context.classLoader.loadClass(enabledClazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BasePreferenceEnabledVerifier
        }

        array.recycle()

        dialogMessage = summary
        dialogTitle = title
    }

    override fun onAttached() {
        super.onAttached()

        init(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.icon_frame.apply {
            (background as StateListDrawable).apply {
                val drawable = getStateDrawable(1)

                if (iconColor != Int.MIN_VALUE) {
                    drawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP)
                } else {
                    drawable.clearColorFilter()
                }
            }
        }
    }
}