package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*

class NonPersistentActivityPreference(context: Context, attrs: AttributeSet) : ActivityPreference(context, attrs), INoPersistPreference

open class ActivityPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs), IColorPreference by ColorPreference(
    context,
    attrs
), IVerifierPreference by VerifierPreference(context, attrs) {
    private val activityIntent: Intent?

    init {
        layoutResource = R.layout.custom_preference

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ActivityPreference, 0, 0)

        activityIntent = run {
            val c = array.getString(R.styleable.ActivityPreference_activity_class)
            if (c != null) try {
                Intent(context, context.classLoader.loadClass(c))
            } catch (e: Exception) {
                null
            } else null
        }

        initVerify(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        bindVH(holder)
    }

    override fun onClick() {
        super.onClick()

        if (activityIntent != null) context.startActivity(activityIntent)
    }
}