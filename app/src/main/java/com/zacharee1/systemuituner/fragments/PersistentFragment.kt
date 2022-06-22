package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.CustomPersistentOption
import com.zacharee1.systemuituner.data.PersistentOption
import com.zacharee1.systemuituner.data.SearchIndex
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.CustomPersistentOptionWidgetBinding
import com.zacharee1.systemuituner.dialogs.CustomPersistentOptionDialogFragment
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.interfaces.*
import com.zacharee1.systemuituner.prefs.InlineActivityPreference
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class PersistentFragment : BasePrefFragment(), SearchView.OnQueryTextListener, SharedPreferences.OnSharedPreferenceChangeListener {
    override val widgetLayout: Int = Int.MIN_VALUE

    private var currentQuery: String? = null

    private val preferences = ArrayList<PersistentPreference>()
    private val isLoaded = async {
        SearchIndex.toInflate.forEach {
            inflate(it.first)
        }

//        preferences.addAll(requireContext().prefManager.customPersistentOptions.map {
//            PersistentPreference.fromCustomPersistentOption(requireContext(), it)
//        })
        true
    }

    @SuppressLint("RestrictedApi")
    private fun inflate(resource: Int): PreferenceScreen {
        return preferenceManager.inflateFromResource(requireContext(), resource, null).also { process(it) }
    }

    private fun process(group: PreferenceGroup) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is PreferenceGroup) process(child)
            else {
                if (child is INoPersistPreference) continue
                preferences.add(PersistentPreference.fromPreference(false, child, this))
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)

        preferenceScreen.removeAll()
        preferenceScreen.addPreference(
            InlineActivityPreference(
                requireContext(),
                Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com"))
            ).apply {
                title = resources.getString(R.string.persistent_options_not_sticking_title)
                summary = resources.getString(R.string.persistent_options_not_sticking_desc)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_help_outline_24)
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !Settings.canDrawOverlays(requireContext())) {
            preferenceScreen.addPreference(
                InlineActivityPreference(
                    requireContext(),
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                ).apply {
                    title = resources.getString(R.string.intro_system_alert_window)
                    summary = resources.getString(R.string.intro_system_alert_window_desc)
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_help_outline_24)
                }
            )
        }
        filterPersistent(null) {
            it.forEach { pref ->
                preferenceScreen.addPreference(construct(pref))
            }
        }

        preferenceScreen.isOrderingAsAdded = false
        requireContext().prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PrefManager.CUSTOM_PERSISTENT_OPTIONS) {
            onQueryTextChange(currentQuery)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.search_bg)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        currentQuery = newText
        filterPersistent(newText) {
            val toRemove = ArrayList<Preference>()

            preferenceScreen.forEach { child ->
                if (!it.map { c -> c.key }.contains(child.key)) {
                    toRemove.add(child)
                }
            }

            toRemove.forEach { pref ->
                preferenceScreen.removePreference(pref)
            }

            it.forEach { pref ->
                if (!preferenceScreen.hasPreference(pref.key)) {
                    preferenceScreen.addPreference(construct(pref))
                }
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        requireContext().prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun addOrEditCustomItem(label: String? = null, key: String? = null, value: String? = null, type: SettingsType? = null) {
        val fragment = if (type == null || key == null || label == null) CustomPersistentOptionDialogFragment()
        else CustomPersistentOptionDialogFragment.forEdit(
            label, key, value, type
        )
        fragment.setTargetFragment(this, 0)
        fragment.show(parentFragmentManager, null)
    }

    private fun filterPersistent(query: String?, result: (ArrayList<PersistentPreference>) -> Unit) = launch {
        isLoaded.await()

        val filter = async {
            ArrayList(
                preferences.filter {
                    (query == null || query.isBlank() ||
                            it.title.toString().contains(query, true) ||
                            it.origSummary?.toString()?.contains(query, true) == true)
                }.map { PersistentPreference.fromPreference(false, it, this@PersistentFragment) } +
                        requireContext().prefManager.customPersistentOptions.filter {
                            query == null || query.isBlank() ||
                                    it.label.contains(query, true) ||
                                    it.key.contains(query, true)
                        }.map {
                            PersistentPreference.fromCustomPersistentOption(this@PersistentFragment, it)
                        }
            )
        }

        result(filter.await())
    }

    private fun construct(pref: PersistentPreference): PersistentPreference {
        return PersistentPreference.copy(pref, this).apply {
            val options = context.prefManager.persistentOptions
            isChecked = keys.all { key ->
                key.value.all { k ->
                    options.any { opt -> opt.key == k && opt.type == key.key }
                }
            }

            setOnPreferenceChangeListener { preference, newValue ->
                preference as PersistentPreference

                if (newValue.toString().toBoolean()) {
                    context.prefManager.apply {
                        persistentOptions = persistentOptions.apply {
                            preference.keys.forEach { (t, ks) ->
                                ks.forEach { k ->
                                    add(PersistentOption(t, k))
                                }
                            }
                        }
                    }
                } else {
                    context.prefManager.apply {
                        persistentOptions = persistentOptions.apply {
                            removeAll { item -> preference.keys.keys.contains(item.type) && preference.keys[item.type]?.contains(item.key) == true }
                        }
                    }
                }

                mainHandler.post {
                    (listView?.adapter as PreferenceGroupAdapter?)?.updatePreferences()
                }

                true
            }
        }
    }

    class PersistentPreference(val isCustom: Boolean, val fragment: PersistentFragment, context: Context = fragment.requireContext()) : CheckBoxPreference(context), ISecurePreference by SecurePreference(
        context,
        null
    ), IColorPreference by ColorPreference(
        context,
        null
    ), IVerifierPreference by VerifierPreference(context, null) {
        companion object {
            fun fromCustomPersistentOption(fragment: PersistentFragment, info: CustomPersistentOption): PersistentPreference {
                return PersistentPreference(true, fragment).apply {
                    title = info.label
                    key = info.key
                    type = info.type
                    keys[type] = arrayOf(key)
                }
            }
            fun fromPreference(isCustom: Boolean, preference: Preference, fragment: PersistentFragment): PersistentPreference {
                return PersistentPreference(isCustom, fragment).apply {
                    title = preference.title
                    icon = preference.icon
                    key = preference.key
                    isVisible = preference.isVisible
                    origSummary = if (preference is PersistentPreference) {
                        preference.origSummary
                    } else {
                        preference.summary
                    }
                    if (preference is PersistentPreference) {
                        keys.putAll(preference.keys)
                    }
                    if (preference is ISpecificPreference) {
                        keys.putAll(preference.keys)
                    }
                    if (preference is IDangerousPreference) {
                        dangerous = preference.dangerous
                    }
                    if (preference is ISecurePreference) {
                        type = preference.type
                    }
                    if (preference is IColorPreference) {
                        iconColor = preference.iconColor
                    }
                    if (preference is IVerifierPreference) {
                        lowApi = preference.lowApi
                        highApi = preference.highApi
                        visibilityVerifier = preference.visibilityVerifier
                        enabledVerifier = preference.enabledVerifier
                    }

                    if (keys.isEmpty()) {
                        keys[type] = keys[type].run {
                            if (this == null) {
                                arrayOf(preference.key)
                            } else {
                                this + arrayOf(preference.key)
                            }
                        }
                    }

                    initSecure(this)
                    initVerify(this)
                }
            }

            fun copy(preference: PersistentPreference, fragment: PersistentFragment): PersistentPreference {
                return fromPreference(preference.isCustom, preference, fragment)
            }
        }

        val keys = HashMap<SettingsType, Array<String>>()
        var origSummary: CharSequence? = null

        init {
            isPersistent = false
            layoutResource = R.layout.custom_preference
            widgetLayoutResource = if (isCustom) R.layout.custom_persistent_option_widget else R.layout.checkbox
        }

        override var dangerous: Boolean = false
            set(value) {
                field = value
                markDangerous()
            }

        override fun isPersistent(): Boolean {
            return false
        }

        override fun compareTo(other: Preference): Int {
            val sup = super.compareTo(other)

            return if (other is TwoStatePreference) {
                if (isChecked && !other.isChecked) -1
                else if (isChecked && other.isChecked) sup
                else if (!isChecked && other.isChecked) 1
                else sup
            } else sup
        }

        override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
            super.onAttachedToHierarchy(preferenceManager)

            if (isCustom) {
                summary = context.prefManager.customPersistentOptions.find { it.type == type && it.key == key }?.run {
                    context.resources.getString(R.string.custom_persistent_option_summary_template, type, key, value)
                }
            }
        }

        override fun onBindViewHolder(holder: PreferenceViewHolder) {
            super.onBindViewHolder(holder)

            if (isCustom) {
                holder.itemView.apply {
                    val binding = CustomPersistentOptionWidgetBinding.bind(findViewById(R.id.check_wrapper))

                    binding.removeButton.setOnClickListener {
                        RoundedBottomSheetDialog(context).apply {
                            setTitle(R.string.remove_item)
                            setMessage(R.string.remove_item_desc)
                            setPositiveButton(android.R.string.ok) { _, _ ->
                                context.prefManager.apply {
                                    persistentOptions = persistentOptions.apply {
                                        removeAll { it.type == type && it.key == key }
                                    }
                                    customPersistentOptions = customPersistentOptions.apply {
                                        removeAll { it.type == type && it.key == key }
                                    }
                                }
                                fragment.preferenceScreen.removePreference(this@PersistentPreference)
                                dismiss()
                            }
                            setNegativeButton(android.R.string.cancel, null)
                        }.show()
                    }

                    binding.editButton.setOnClickListener {
                        val customInfo = context.prefManager.customPersistentOptions.find {
                            it.key == key && it.type == type
                        } ?: return@setOnClickListener

                        fragment.addOrEditCustomItem(customInfo.label, customInfo.key, customInfo.value, customInfo.type)
                    }
                }
            }

            bindVH(holder)
        }

        fun copy(): PersistentPreference {
            return fromPreference(isCustom,this, fragment)
        }

        private fun markDangerous() {
            title = if (dangerous) {
                SpannableString(title).apply {
                    setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
                }
            } else {
                title.toString()
            }
        }
    }
}