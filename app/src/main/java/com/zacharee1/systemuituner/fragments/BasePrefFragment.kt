package com.zacharee1.systemuituner.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import at.blogc.android.views.ExpandableTextView
import com.google.android.material.card.MaterialCardView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.anim.PrefAnimator
import com.zacharee1.systemuituner.data.PreferenceHolder
import com.zacharee1.systemuituner.dialogs.*
import com.zacharee1.systemuituner.interfaces.IDangerousPreference
import com.zacharee1.systemuituner.prefs.*
import com.zacharee1.systemuituner.prefs.demo.DemoListPreference
import com.zacharee1.systemuituner.prefs.demo.DemoSeekBarPreference
import com.zacharee1.systemuituner.prefs.demo.DemoSwitchPreference
import com.zacharee1.systemuituner.prefs.secure.SecureEditTextPreference
import com.zacharee1.systemuituner.prefs.secure.SecureListPreference
import com.zacharee1.systemuituner.prefs.secure.SecureSeekBarPreference
import com.zacharee1.systemuituner.prefs.secure.SecureSwitchPreference
import com.zacharee1.systemuituner.prefs.secure.specific.*
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.*

abstract class BasePrefFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {
    companion object {
        const val ARG_HIGHLIGHT_KEY = "highlight_key"
    }

    private val highlightKey: String?
        get() = arguments?.getString(ARG_HIGHLIGHT_KEY)

    open val widgetLayout: Int = Int.MIN_VALUE
    open val limitSummary = true
    open val supportsGrid = true

    open val paddingDp = arrayOf(8f, 8f, 8f, 8f)
    open val preferencePadding: ((Preference) -> Array<Float>)? = null

    open val hasCategories = false

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val fragment = when (preference) {
            is ForceEnableAllPreference -> SwitchOptionDialog.newInstance(
                preference.key,
                preference.disabled,
                preference.enabled,
                requireContext().prefManager.forceEnableAll
            )
            is SecureSwitchPreference -> SwitchOptionDialog.newInstance(
                preference.key,
                preference.disabled,
                preference.enabled,
                requireContext().getSetting(preference.type, preference.writeKey, preference.defaultValue) == preference.enabled
            )
            is SecureSeekBarPreference -> SeekBarOptionDialog.newInstance(
                preference.key,
                preference.minValue,
                preference.maxValue,
                preference.defaultValue,
                preference.units,
                preference.scale,
                (((requireContext().getSetting(preference.type, preference.writeKey)?.toFloatOrNull()
                    ?: (preference.defaultValue * preference.scale)) / preference.scale)).toInt()
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
            is DemoListPreference -> SecureListDialog.newInstance(preference.key)
            is DemoSeekBarPreference -> SeekBarOptionDialog.newInstance(
                preference.key,
                preference.minValue,
                preference.maxValue,
                preference.defaultValue,
                preference.units,
                preference.scale,
                (preference.sharedPreferences!!.getFloat(preference.key, preference.defaultValue * preference.scale) / preference.scale).toInt()
            )
            is DemoSwitchPreference -> SwitchOptionDialog.newInstance(
                preference.key,
                preference.disabled,
                preference.enabled,
                preference.sharedPreferences!!.getString(preference.key, preference.defaultValue?.toString()) == preference.enabled
            )
            is ReadSettingPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.dialog_read_setting
            )
            is WriteSettingPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.dialog_write_setting
            )
            is OneUIClockPositionPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.one_ui_clock_position
            )
            is TouchWizNavigationBarColor -> OptionDialog.newInstance(
                preference.key,
                R.layout.touchwiz_navigation_bar_color_dialog
            )
            is NotificationSnoozeTimesPreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.notification_snooze_times
            )
            is NightModePreference -> OptionDialog.newInstance(
                preference.key,
                R.layout.night_mode
            )
            else -> null
        }

        fragment?.setTargetFragment(this, 0)
        fragment?.show(parentFragmentManager, null)

        if (fragment == null) {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onBindPreferences() {
        markDangerous(preferenceScreen)
        super.onBindPreferences()

        setDivider(ResourcesCompat.getDrawable(resources, R.drawable.custom_divider, requireContext().theme))
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                updateLayoutManager(view, listView, grid, linear, supportsGrid)
                updateListWidthAndGravity()
            }
        }

        highlightKey?.let { hKey ->
            listView?.post {
                listView?.apply {
                    val a = adapter as PreferenceGroupAdapter
                    val index = a.getPreferenceAdapterPosition(hKey)

                    scrollToPosition(index)

                    launch {
                        val item = layoutManager?.findViewByPosition(index) as? MaterialCardView ?: return@launch

                        delay(200)
                        val time = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                        item.isPressed = true

                        val anim = ObjectAnimator.ofPropertyValuesHolder(
                            item,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
                        )
                        anim.repeatCount = 1
                        anim.repeatMode = ValueAnimator.REVERSE
                        anim.duration = time
                        anim.interpolator = AnticipateInterpolator()
                        anim.doOnEnd {
                            item.isPressed = false
                        }
                        anim.start()
                    }
                }
            }
        }

        updateListWidthAndGravity()
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?
    ): RecyclerView {
        return super.onCreateRecyclerView(inflater, parent, savedInstanceState).also {
            requireContext().apply {
                it.setPaddingRelative(dpAsPx(paddingDp[0]), dpAsPx(paddingDp[1]), dpAsPx(paddingDp[2]), dpAsPx(paddingDp[3]))
            }
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

    open fun onBindViewHolder(holder: PreferenceViewHolder, position: Int, preference: Preference?) {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            private val descriptors = ArrayList<PreferenceHolder>()

            @SuppressLint("RestrictedApi")
            override fun getItemViewType(position: Int): Int {
                val descriptor = PreferenceHolder(getItem(position)!!)
                val index = descriptors.indexOf(descriptor)

                return if (index != -1) {
                    index
                } else {
                    descriptors.add(descriptor)
                    descriptors.lastIndex
                }
            }

            @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                val preference = getItem(position)

                if (hasCategories) {
                    if (chooseLayoutManagerWithoutSetting(view, grid, linear) == grid && holder.itemView.layoutParams !is StaggeredGridLayoutManager.LayoutParams) {
                        holder.itemView.layoutParams = StaggeredGridLayoutManager.LayoutParams(holder.itemView.layoutParams).apply {
                            isFullSpan = getItem(position) is PreferenceCategory
                        }
                    }
                }

                (holder.itemView as ViewGroup).apply {
                    context.apply {
                        preference?.let {
                            preferencePadding?.invoke(preference)?.apply {
                                setPaddingRelative(
                                    dpAsPx(this[0]),
                                    dpAsPx(this[1]),
                                    dpAsPx(this[2]),
                                    dpAsPx(this[3]),
                                )
                            }
                        }
                    }

                    val summaryView = findViewById<TextView>(android.R.id.summary)

                    summaryView.post {
                        findViewById<View>(R.id.expand_summary)?.apply {
                            val image = findViewById<ImageView>(R.id.expand_summary_icon)

                            summaryView as ExpandableTextView
                            isVisible = summaryView.lineCount > summaryView.maxLines || summaryView.hasEllipsis

                            image.rotation = if (!summaryView.isExpanded) 0f else 180f
                            setOnClickListener {
                                if (summaryView.isExpanded) {
                                    image.animate()
                                        .rotation(0f)
                                        .withEndAction {
                                            image.rotation = 0f
                                        }
                                    summaryView.collapse()
                                } else {
                                    image.animate()
                                        .rotation(180f)
                                        .withEndAction {
                                            image.rotation = 180f
                                        }
                                    summaryView.expand()
                                }
                            }
                        }
                    }
                }

                this@BasePrefFragment.onBindViewHolder(holder, position, preference)
            }

            @SuppressLint("RestrictedApi", "PrivateResource")
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PreferenceViewHolder {
                val item = descriptors[viewType]
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
                        inflater.inflate(item.layoutResource, parent, false)
                    if (view.background == null) {
                        ViewCompat.setBackground(view, background)
                    }

                    val widgetFrame =
                        view.findViewById<ViewGroup>(android.R.id.widget_frame)
                    if (widgetFrame != null) {
                        when {
                            widgetLayout != Int.MIN_VALUE -> {
                                inflater.inflate(widgetLayout, widgetFrame)
                            }
                            item.widgetLayoutResource != 0 -> {
                                inflater.inflate(item.widgetLayoutResource, widgetFrame)
                            }
                            else -> {
                                widgetFrame.visibility = View.GONE
                            }
                        }
                    }

                    if (item.className != PreferenceGroup::class.java.canonicalName) {
                        if (item.isEnabled && limitSummary) {
                            view.findViewById<TextView>(android.R.id.summary).apply {
                                maxLines = 2
                                ellipsize = TextUtils.TruncateAt.END
                            }
                        }
                    }

                    PreferenceViewHolder.createInstanceForTests(view)
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

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return chooseLayoutManager(view, grid, linear, supportsGrid)
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }

    private fun markDangerous(group: PreferenceGroup) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is IDangerousPreference && child.dangerous) {
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

    private fun updateListWidthAndGravity(widthDp: Float = requireContext().asDp(requireView().width)) {
        if (!supportsGrid) {
            listView.layoutParams = (listView.layoutParams as FrameLayout.LayoutParams).apply {
                width = if (widthDp >= 800) requireContext().dpAsPx(800) else ViewGroup.LayoutParams.MATCH_PARENT
                gravity = if (widthDp >= 800) Gravity.CENTER_HORIZONTAL else Gravity.START
            }
        } else {
            listView.layoutParams = (listView.layoutParams as FrameLayout.LayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.START
            }
        }
    }
}