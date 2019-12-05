package com.rw.tweaks.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.rw.tweaks.R
import com.rw.tweaks.anim.PrefAnimator
import com.rw.tweaks.dialogs.*
import com.rw.tweaks.prefs.secure.SecureEditTextPreference
import com.rw.tweaks.prefs.secure.SecureListPreference
import com.rw.tweaks.prefs.secure.SecureSeekBarPreference
import com.rw.tweaks.prefs.secure.SecureSwitchPreference
import com.rw.tweaks.prefs.secure.specific.*
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.dpAsPx
import com.rw.tweaks.util.mainHandler
import kotlinx.android.synthetic.main.custom_preference.view.*

abstract class BasePrefFragment : PreferenceFragmentCompat() {
    companion object {
        const val ARG_HIGHLIGHT_KEY = "highlight_key"
    }

    private val highlightKey by lazy { arguments?.getString(ARG_HIGHLIGHT_KEY) }

    open val prefLayout: Int = R.layout.custom_preference
    open val widgetLayout: Int = 0
    open val limitSummary = true

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment = when (preference) {
            is SecureSwitchPreference -> SwitchOptionDialog.newInstance(
                preference.key,
                preference.disabled,
                preference.enabled
            )
            is SecureSeekBarPreference -> SeekBarOptionDialog.newInstance(
                preference.key,
                preference.minValue,
                preference.maxValue,
                preference.defaultValue,
                preference.units,
                preference.scale
            )
            is AnimationScalesPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.animation_dialog
            )
            is KeepDeviceOnPluggedPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.keep_device_plugged_dialog
            )
            is StorageThresholdPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.storage_thresholds
            )
            is CameraGesturesPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.camera_gestures
            )
            is AirplaneModeRadiosPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.airplane_mode_radios
            )
            is ImmersiveModePreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.immersive_mode
            )
            is SecureListPreference -> SecureListDialog.newInstance(preference.key)
            is UISoundsPreference -> OptionDialog.newInstance(preference.key, R.layout.ui_sounds)
            is TetheringPreference -> SwitchOptionDialog.newInstance(
                preference.key,
                "false",
                "true",
                preference.bothFixed
            )
            is SMSLimitsPreference -> OptionDialog.newInstance(preference.key, R.layout.sms_limits)
            is LockscreenShortcutsPref -> OptionDialog.newInstance(
                preference.key,
                R.layout.lockscreen_shortcuts
            )
            is SecureEditTextPreference -> SecureEditTextDialog.newInstance(preference.key)
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

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView {
        return super.onCreateRecyclerView(inflater, parent, savedInstanceState).also {
            val padding = requireContext().dpAsPx(8)

            it.setPaddingRelative(padding, padding, padding, padding)
            it.clipToPadding = false
            it.itemAnimator = PrefAnimator().apply {
                addDuration = 300
                removeDuration = 300
                moveDuration = 0
                changeDuration = 0
            }
            it.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.list_initial_anim)
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {

            override fun getItemViewType(position: Int): Int {
                return position
            }

            @SuppressLint("RestrictedApi")
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                val item = getItem(position)

                if (item is ISecurePreference) {
                    holder.itemView.icon_frame.apply {
                        val color = item.iconColor

                        (background as StateListDrawable).apply {
                            val drawable = getStateDrawable(1)

                            if (color != Int.MIN_VALUE) {
                                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            } else {
                                drawable.clearColorFilter()
                            }
                        }
                    }
                }
            }

            @SuppressLint("RestrictedApi", "PrivateResource")
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PreferenceViewHolder {
                val item = getItem(viewType)
                return run {
                    val inflater = LayoutInflater.from(parent.context)
                    val a = parent.context.obtainStyledAttributes(
                        null,
                        androidx.preference.R.styleable.BackgroundStyle
                    )
                    var background =
                        a.getDrawable(androidx.preference.R.styleable.BackgroundStyle_android_selectableItemBackground)
                    if (background == null) {
                        background = AppCompatResources.getDrawable(
                            parent.context,
                            android.R.drawable.list_selector_background
                        )
                    }
                    a.recycle()

                    val view: View =
                        inflater.inflate(prefLayout, parent, false)
                    if (view.background == null) {
                        ViewCompat.setBackground(view, background)
                    }

                    val widgetFrame =
                        view.findViewById<ViewGroup>(android.R.id.widget_frame)
                    if (widgetFrame != null) {
                        if (widgetLayout != 0) {
                            inflater.inflate(widgetLayout, widgetFrame)
                        } else {
                            widgetFrame.visibility = View.GONE
                        }
                    }

                    val newView = if (item is PreferenceGroup) view else run {
                        val cardView = LayoutInflater.from(parent.context).inflate(
                            R.layout.pref_card,
                            parent,
                            false
                        ) as MaterialCardView

                        cardView.addView(view)
                        cardView.findViewById<TextView>(android.R.id.title).apply {
                            setSingleLine(false)
                        }
                        if (item.isEnabled && limitSummary) {
                            cardView.findViewById<TextView>(android.R.id.summary).apply {
                                maxLines = 2
                                ellipsize = TextUtils.TruncateAt.END
                            }
                        }

                        cardView
                    }

                    PreferenceViewHolder.createInstanceForTests(newView)
                }
            }
        }
    }

    private val grid by lazy {
        object : StaggeredGridLayoutManager(2, VERTICAL) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return true
            }
        }
    }
    private val linear by lazy {
        object : LinearLayoutManager(requireContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return true
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        listView?.layoutManager = if (newConfig.screenWidthDp >= 800)
            grid else linear
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return if (resources.configuration.screenWidthDp >= 800)
            grid else linear
    }

    private fun markDangerous(group: PreferenceGroup) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is ISecurePreference && child.dangerous) {
                markDangerous(child)
            }
            if (child is PreferenceGroup) markDangerous(child)
        }
    }

    private fun markDangerous(preference: Preference) {
        preference.title = SpannableString(preference.title).apply {
            setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
        }
    }
}