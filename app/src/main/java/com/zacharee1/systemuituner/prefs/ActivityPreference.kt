package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*

open class InlineActivityPreference(context: Context, intent: Intent) : Preference(context),
    IColorPreference by ColorPreference(context, null), ISecurePreference by SecurePreference(context, null) {
    init {
        layoutResource = R.layout.custom_preference

        this.intent = intent

        initSecure(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        bindVH(holder)
    }
}

class NonPersistentActivityPreference(context: Context, attrs: AttributeSet) : ActivityPreference(context, attrs), INoPersistPreference

open class ActivityPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs), IColorPreference by ColorPreference(
    context,
    attrs
), IVerifierPreference by VerifierPreference(context, attrs), ISecurePreference by SecurePreference(context, attrs) {
    init {
        layoutResource = R.layout.custom_preference

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ActivityPreference, 0, 0)

        intent = run {
            val c = array.getString(R.styleable.ActivityPreference_activity_class)
            if (c != null) try {
                Intent(context, context.classLoader.loadClass(c))
            } catch (e: Exception) {
                null
            } else null
        }

        initVerify(this)
        initSecure(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        bindVH(holder)
    }
}