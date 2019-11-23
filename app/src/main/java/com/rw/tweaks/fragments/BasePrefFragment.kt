package com.rw.tweaks.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rw.tweaks.R
import com.rw.tweaks.dialogs.OptionDialog
import com.rw.tweaks.dialogs.SecureListDialog
import com.rw.tweaks.dialogs.SeekBarOptionDialog
import com.rw.tweaks.dialogs.SwitchOptionDialog
import com.rw.tweaks.prefs.secure.SecureListPreference
import com.rw.tweaks.prefs.secure.SecureSeekBarPreference
import com.rw.tweaks.prefs.secure.SecureSwitchPreference
import com.rw.tweaks.prefs.secure.specific.*
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.mainHandler

abstract class BasePrefFragment : PreferenceFragmentCompat() {
    companion object {
        const val ARG_HIGHLIGHT_KEY = "highlight_key"
    }

    private val highlightKey by lazy { arguments?.getString(ARG_HIGHLIGHT_KEY) }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment = when (preference) {
            is SecureSwitchPreference -> SwitchOptionDialog.newInstance(preference.key, preference.disabled, preference.enabled)
            is SecureSeekBarPreference -> SeekBarOptionDialog.newInstance(preference.key, preference.minValue, preference.maxValue, preference.defaultValue, preference.units, preference.scale)
            is AnimationScalesPreference -> OptionDialog.newInstance(preference.key, R.layout.animation_dialog)
            is KeepDeviceOnPluggedPreference -> OptionDialog.newInstance(preference.key, R.layout.keep_device_plugged_dialog)
            is StorageThresholdPreference -> OptionDialog.newInstance(preference.key, R.layout.storage_thresholds)
            is CameraGesturesPreference -> OptionDialog.newInstance(preference.key, R.layout.camera_gestures)
            is AirplaneModeRadiosPreference -> OptionDialog.newInstance(preference.key, R.layout.airplane_mode_radios)
            is ImmersiveModePreference -> OptionDialog.newInstance(preference.key, R.layout.immersive_mode)
            is SecureListPreference -> SecureListDialog.newInstance(preference.key)
            is UISoundsPreference -> OptionDialog.newInstance(preference.key, R.layout.ui_sounds)
            is TetheringPreference -> SwitchOptionDialog.newInstance(preference.key, "false", "true", preference.bothFixed)
            is SMSLimitsPreference -> OptionDialog.newInstance(preference.key, R.layout.sms_limits)
            is LockscreenShortcutsPref -> OptionDialog.newInstance(preference.key, R.layout.lockscreen_shortcuts)
            else -> null
        }

        fragment?.setTargetFragment(this, 0)
        fragment?.show(fragmentManager!!, null)

        if (fragment == null) {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onBindPreferences() {
        markDangerous(preferenceScreen)
        super.onBindPreferences()
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (highlightKey != null) {
            mainHandler.post {
                listView.apply {
                    val a = adapter as PreferenceGroupAdapter
                    val index = a.getPreferenceAdapterPosition(highlightKey)

                    scrollToPosition(index)

                    val item = getChildAt(index)

                    mainHandler.postDelayed({
                        item?.isPressed = true

                        mainHandler.postDelayed({ item?.isPressed = false }, 300)
                    }, 200)
                }
            }
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PreferenceViewHolder {
                val vh = super.onCreateViewHolder(parent, viewType)
                val cardView = LayoutInflater.from(parent.context).inflate(R.layout.pref_card, parent, false) as CardView

                cardView.addView(vh.itemView)
                cardView.findViewById<TextView>(android.R.id.title).apply {
                    setSingleLine(false)
                }
                cardView.findViewById<TextView>(android.R.id.summary).apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                }

                return PreferenceViewHolder.createInstanceForTests(cardView)
            }
        }
    }

    private val grid by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }
    private val linear by lazy { LinearLayoutManager(requireContext()) }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        listView?.layoutManager = if (newConfig.screenWidthDp >= 800)
            grid else linear
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return if (resources.configuration.screenWidthDp >= 800)
            grid else linear
    }

    internal fun markDangerous(group: PreferenceGroup) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is ISecurePreference && child.dangerous) {
                markDangerous(child)
            }
            if (child is PreferenceGroup) markDangerous(child)
        }
    }

    internal fun markDangerous(preference: Preference) {
        preference.title = SpannableString(preference.title).apply {
            setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
        }
    }
}