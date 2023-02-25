package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.intro.ComposeIntroActivity
import com.zacharee1.systemuituner.anim.PrefAnimator
import com.zacharee1.systemuituner.data.BlacklistBackupInfo
import com.zacharee1.systemuituner.data.CustomBlacklistItemInfo
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.dialogs.CustomBlacklistItemDialogFragment
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.interfaces.ColorPreference
import com.zacharee1.systemuituner.interfaces.IColorPreference
import com.zacharee1.systemuituner.prefs.BlacklistBrokenBatteryAndroid10Preference
import com.zacharee1.systemuituner.prefs.BlacklistPreference
import com.zacharee1.systemuituner.prefs.BlacklistRotationLockPreference
import com.zacharee1.systemuituner.prefs.CustomBlacklistAddPreference
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.*
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceCategoryNew
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi")
class IconBlacklistFragment : CoroutinePreferenceFragment(), SearchView.OnQueryTextListener,
    SearchView.OnCloseListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val origExpansionStates = HashMap<String, Boolean>()
    private val gson = GsonBuilder().create()

    private val backupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/*")) { result ->
        result?.let { uri ->
            requireContext().contentResolver.openOutputStream(uri)?.use { out ->
                OutputStreamWriter(out).use { writer ->
                    writer.appendLine(
                        gson.toJson(
                            BlacklistBackupInfo(
                                requireContext().prefManager.blacklistedItems,
                                requireContext().prefManager.customBlacklistItems
                            )
                        )
                    )
                }
            }
        }
    }

    private val restoreLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
        result?.let { uri ->
            try {
                val lines = ArrayList<String>()

                requireContext().contentResolver.openInputStream(uri)?.use { input ->
                    InputStreamReader(input).use { reader ->
                        reader.forEachLine { line ->
                            lines.add(line)
                        }
                    }
                }

                if (lines.isNotEmpty()) {
                    if (lines.size > 1) {
                        throw Exception("Invalid format!")
                    }

                    val firstLine = lines[0]

                    val info = try {
                        gson.fromJson<BlacklistBackupInfo>(
                            firstLine,
                            object : TypeToken<BlacklistBackupInfo>() {}.type
                        )
                    } catch (e: Exception) {
                        null
                    }

                    launch {
                        if (info != null) {
                            requireContext().apply {
                                prefManager.blacklistedItems = info.items
                                prefManager.customBlacklistItems = info.customItems
                                writeSetting(SettingsType.SECURE, "icon_blacklist", info.items.joinToString(","))
                            }
                        } else {
                            requireContext().prefManager.blacklistedItems =
                                HashSet(firstLine.split(","))
                            requireContext().writeSetting(SettingsType.SECURE, "icon_blacklist", firstLine)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(
                        R.string.error_restoring_icon_blacklist,
                        e.localizedMessage
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_blacklist, rootKey)

        requireContext().prefManager.prefs.registerOnSharedPreferenceChangeListener(this)

        createCategory(R.string.backup_restore, "backup_restore_icon_blacklist") {
            it.addPreference(
                BackupRestorePreference(requireContext()).apply {
                    key = "backup_blacklist"
                    iconColor = requireContext().getColor(R.color.pref_color_2)
                    setIcon(R.drawable.ic_baseline_save_24)
                    setTitle(R.string.back_up)
                    setOnPreferenceClickListener {
                        val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
                        try {
                            backupLauncher.launch("icon_blacklist_${formatter.format(Date())}.suit")
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(requireContext(), resources.getString(R.string.error_creating_file_template, e.localizedMessage), Toast.LENGTH_SHORT).show()
                        }

                        true
                    }
                }
            )
            it.addPreference(
                BackupRestorePreference(requireContext()).apply {
                    key = "restore_blacklist"
                    iconColor = requireContext().getColor(R.color.pref_color_3)
                    setIcon(R.drawable.ic_baseline_restore_24)
                    setTitle(R.string.restore)
                    setOnPreferenceClickListener {
                        try {
                            restoreLauncher.launch(arrayOf("*/*"))
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(requireContext(), resources.getText(R.string.error_restoring_icon_blacklist, e.localizedMessage), Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                }
            )
        }

        createCategory(R.string.category_icon_blacklist_general, "icon_blacklist_general") {
            it.createPref(R.string.icon_blacklist_airplane, key = "airplane")
            it.createPref(R.string.icon_blacklist_vpn, key = "vpn")
            it.createPref(
                R.string.icon_blacklist_volte,
                key = "volte",
                additionalKeys = arrayOf("ims_volte")
            )
            it.createPref(R.string.icon_blacklist_wifi_calling, key = "wifi_calling")
            it.createPref(R.string.icon_blacklist_vowifi, key = "vowifi")
            it.createPref(R.string.icon_blacklist_dmb, key = "dmb")
            it.createPref(R.string.icon_blacklist_cdma_eri, key = "cdma_eri")
            it.createPref(R.string.icon_blacklist_data_connection, key = "data_connection")
            it.createPref(R.string.icon_blacklist_phone_evdo_signal, key = "phone_evdo_signal")
            it.createPref(R.string.icon_blacklist_phone_signal, key = "phone_signal")
            it.createPref(R.string.icon_blacklist_volume, key = "volume")
            it.createPref(R.string.icon_blacklist_headset, key = "headset")
            it.createPref(R.string.icon_blacklist_speakerphone, key = "speakerphone")
            it.createPref(R.string.icon_blacklist_remote_call, key = "remote_call")
            it.createPref(R.string.icon_blacklist_tty, key = "tty")
            it.createPref(R.string.icon_blacklist_clock, key = "clock")
            it.createPref(
                R.string.icon_blacklist_alarm,
                key = "alarm",
                additionalKeys = arrayOf("alarm_clock")
            )
            it.createPref(R.string.icon_blacklist_zen, key = "zen")
            it.createPref(
                R.string.icon_blacklist_do_not_disturb,
                key = "do_not_disturb",
                additionalKeys = arrayOf("dnd")
            )
            it.createPref(R.string.icon_blacklist_managed_profile, key = "managed_profile")
            it.createPref(
                R.string.icon_blacklist_nfc,
                key = "nfc",
                additionalKeys = arrayOf("nfc_on")
            )
            it.createPref(R.string.icon_blacklist_cast, key = "cast")
            it.createPref(R.string.icon_blacklist_battery, key = "battery")
            it.createPref(R.string.icon_blacklist_location, key = "location")
            it.createPref(R.string.icon_blacklist_su, key = "su")
            it.createPref(R.string.icon_blacklist_otg_mouse, key = "otg_mouse")
            it.createPref(R.string.icon_blacklist_otg_keyboard, key = "otg_keyboard")
            it.createPref(R.string.icon_blacklist_felica_lock, key = "felica_lock")
            it.createPref(R.string.icon_blacklist_answering_memo, key = "answering_memo")
            it.createPref(R.string.icon_blacklist_ime, key = "ime")
            it.createPref(R.string.icon_blacklist_sync_failing, key = "sync_failing")
            it.createPref(R.string.icon_blacklist_sync_active, key = "sync_active")
            it.createPref(R.string.icon_blacklist_nfclock, key = "nfclock")
            it.createPref(R.string.icon_blacklist_secure, key = "secure")
            it.createPref(R.string.icon_blacklist_power_saver, key = "power_saver")
            it.createPref(R.string.icon_blacklist_data_saver, key = "data_saver")
            it.createPref(R.string.icon_blacklist_hotspot, key = "hotspot")
            it.createPref(R.string.icon_blacklist_bluetooth, key = "bluetooth")
            it.createPref(R.string.icon_blacklist_mute, key = "mute")
            it.createPref(R.string.icon_blacklist_rotate, key = "rotate")
        }

        if (requireContext().isTouchWiz) {
            createCategory(R.string.category_icon_blacklist_samsung, "icon_blacklist_samsung") {
                it.createPref(R.string.icon_blacklist_knox_container, key = "knox_container")
                it.createPref(R.string.icon_blacklist_smart_network, key = "smart_network")
                it.createPref(R.string.icon_blacklist_glove, key = "glove")
                it.createPref(R.string.icon_blacklist_gesture, key = "gesture")
                it.createPref(R.string.icon_blacklist_smart_scroll, key = "smart_scroll")
                it.createPref(R.string.icon_blacklist_face, key = "face")
                it.createPref(R.string.icon_blacklist_gps, key = "gps")
                it.createPref(R.string.icon_blacklist_lbs, key = "lbs")
                it.createPref(R.string.icon_blacklist_wearable_gear, key = "wearable_gear")
                it.createPref(R.string.icon_blacklist_femtoicon, key = "femtoicon")
                it.createPref(R.string.icon_blacklist_comsamsungrcs, key = "com.samsung.rcs")
                it.createPref(R.string.icon_blacklist_wifi_p2p, key = "wifi_p2p")
                it.createPref(R.string.icon_blacklist_wifi_ap, key = "wifi_ap")
                it.createPref(R.string.icon_blacklist_wifi_oxygen, key = "wifi_oxygen")
                it.createPref(
                    R.string.icon_blacklist_phone_signal_second_stub,
                    key = "phone_signal_second_stub"
                )
                it.createPref(R.string.icon_blacklist_toddler, key = "toddler")
                it.createPref(R.string.icon_blacklist_keyguard_wakeup, key = "keyguard_wakeup")
                it.createPref(R.string.icon_blacklist_safezone, key = "safezone")
                it.createPref(R.string.icon_blacklist_wimax, key = "wimax")
                it.createPref(R.string.icon_blacklist_smart_bonding, key = "smart_bonding")
                it.createPref(R.string.icon_blacklist_private_mode, key = "private_mode")
            }
        }

        if (isHuawei) {
            createCategory(R.string.category_icon_blacklist_huawei, "icon_blacklist_huawei") {
                it.createPref(R.string.icon_blacklist_powersavingmode, key = "powersavingmode")
                it.createPref(R.string.icon_blacklist_earphone, key = "earphone")
                it.createPref(R.string.icon_blacklist_volte_call, key = "volte_call")
                it.createPref(R.string.icon_blacklist_unicom_call, key = "unicom_call")
                it.createPref(R.string.icon_blacklist_eyes_protect, key = "eyes_protect")
            }
        }

        if (isXiaomi) {
            createCategory(R.string.category_icon_blacklist_xiaomi, "icon_blacklist_xiaomi") {
                it.createPref(R.string.icon_blacklist_mikey, key = "mikey")
                it.createPref(R.string.icon_blacklist_call_record, key = "call_record")
                it.createPref(R.string.icon_blacklist_privacy_mode, key = "privacy_mode")
                it.createPref(R.string.icon_blacklist_ble_unlock_mode, key = "ble_unlock_mode")
                it.createPref(R.string.icon_blacklist_quiet, key = "quiet")
                it.createPref(R.string.icon_blacklist_gps, key = "gps")
                it.createPref(R.string.icon_blacklist_missed_call, key = "missed_call")
                it.createPref(
                    R.string.icon_blacklist_bluetooth_handsfree_battery,
                    key = "bluetooth_handsfree_battery"
                )
                it.createPref(R.string.icon_blacklist_wimax, key = "wimax")
            }
        }

        if (isHTC) {
            createCategory(R.string.category_icon_blacklist_htc, "icon_blacklist_htc") {
                it.createPref(R.string.icon_blacklist_femtoicon, key = "femtoicon")
            }
        }

        if (isLG) {
            createCategory(R.string.category_icon_blacklist_lg, "icon_blacklist_lg") {
                it.createPref(R.string.icon_blacklist_rtt, key = "rtt")
            }
        }

        createCategory(R.string.category_icon_blacklist_custom, "icon_blacklist_custom") {
            buildCustomCategory(it)
        }

        createCategory(
            R.string.category_icon_blacklist_auto,
            "icon_blacklist_auto",
            resources.getText(R.string.category_icon_blacklist_auto_desc),
            null
        ).apply {
            isPersistent = false
            onExpandChangeListener = {
                if (preferenceCount == 0 && it) {
                    if (!requireContext().run { hasDump && hasPackageUsageStats }) {
                        ComposeIntroActivity.start(requireContext(), arrayOf(ComposeIntroActivity.Companion.StartReason.EXTRA_PERMISSIONS))
                        expanded = false
                    } else {
                        launch {
                            val icons = withContext(Dispatchers.Main) {
                                parseAutoIconBlacklistSlots()
                            }

                            icons.forEach { key ->
                                createPref(title = key, key = "auto_key_$key", autoWriteKey = key)
                            }

                            generateSummary(it)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
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
            it.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.list_initial_anim)
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val fragment = when (preference) {
            is CustomBlacklistAddPreference -> CustomBlacklistItemDialogFragment.newInstance(
                preference.key
            )
            is BlacklistBrokenBatteryAndroid10Preference -> {
                RoundedBottomSheetDialog(requireContext()).apply {
                    setTitle(preference.title)
                    setMessage(preference.summary)
                    setPositiveButton(
                        R.string.hide_battery_android_bug_btn_view_issue
                    ) { _, _ ->
                        requireContext().launchUrl("https://issuetracker.google.com/issues/141806620")
                    }
                    setNegativeButton(android.R.string.cancel, null)
                    show()
                }
                return
            }
            is BlacklistRotationLockPreference -> {
                RoundedBottomSheetDialog(requireContext()).apply {
                    setTitle(preference.title)
                    setMessage(preference.summary)
                    setPositiveButton(android.R.string.ok, null)
                    show()
                }
                return
            }
            else -> null
        }

        @Suppress("DEPRECATION")
        fragment?.setTargetFragment(this, 0)
        fragment?.show(parentFragmentManager, null)

        if (fragment == null) {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText, preferenceScreen)
        return true
    }

    override fun onClose(): Boolean {
        origExpansionStates.forEach { (t, u) ->
            findPreference<CollapsiblePreferenceCategoryNew>(t)?.expanded = u
        }

        origExpansionStates.clear()
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PrefManager.CUSTOM_BLACKLIST_ITEMS -> findPreference<CollapsiblePreferenceCategoryNew>("icon_blacklist_custom")?.let { buildCustomCategory(it) }
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
        return chooseLayoutManager(view, grid, linear)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                updateLayoutManager(view, listView, grid, linear)
            }
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                if (chooseLayoutManagerWithoutSetting(view, grid, linear) == grid && holder.itemView.layoutParams !is StaggeredGridLayoutManager.LayoutParams) {
                    holder.itemView.layoutParams = StaggeredGridLayoutManager.LayoutParams(holder.itemView.layoutParams).apply {
                        isFullSpan = getItem(position) is PreferenceCategory
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        requireContext().prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun filter(query: String?, group: PreferenceGroup) {
        group.forEach { child ->
            if (child is PreferenceGroup) {
                if (child is CollapsiblePreferenceCategoryNew) {
                    if (!origExpansionStates.containsKey(child.key)) origExpansionStates[child.key] =
                        child.expanded
                    if (!child.expanded) child.expanded = true
                }

                if (!child.isVisible) child.isVisible = true

                filter(query, child)
            } else {
                child.isVisible = matches(query, child)
            }
        }
    }

    private fun matches(query: String?, pref: Preference): Boolean {
        return query.isNullOrBlank() || pref.title?.contains(query, true) == true
    }

    private fun createCategory(
        title: Int,
        key: String,
        initialSummary: CharSequence? = null,
        children: ((CollapsiblePreferenceCategoryNew) -> Unit)?
    ): CollapsiblePreferenceCategoryNew {
        return object : CollapsiblePreferenceCategoryNew(requireContext(), null) {
            init {
                setTitle(title)
                this.key = key
                this.summary = initialSummary

                isIconSpaceReserved = false
                arrowSide = ArrowSide.END

                preferenceScreen.addPreference(this)
                children?.invoke(this)
            }

            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)

                holder.isDividerAllowedAbove = false
                holder.isDividerAllowedBelow = false
            }
        }
    }

    private fun CollapsiblePreferenceCategoryNew.createPref(
        titleRes: Int = 0,
        title: String? = null,
        key: String,
        additionalKeys: Array<String> = arrayOf(),
        autoWriteKey: String? = null,
        isCustom: Boolean = false
    ): BlacklistPreference {
        return object : BlacklistPreference(requireContext(), null) {
            init {
                if (titleRes != 0) {
                    setTitle(titleRes)
                } else {
                    this.title = title
                }
                this.key = key
                this.addAdditionalKeys(additionalKeys.toList())
                this.autoWriteKey = autoWriteKey

                if (isCustom) summary = key

                addPreference(this)
                order = Preference.DEFAULT_ORDER

                setOnPreferenceChangeListener { _, newValue ->
                    val isChecked = newValue.toString().toBoolean()

                    val currentlyBlacklisted = HashSet(context.getSetting(SettingsType.SECURE, "icon_blacklist")?.split(",") ?: HashSet<String>())

                    if (!isChecked) {
                        currentlyBlacklisted.addAll(allKeys)
                    } else {
                        currentlyBlacklisted.removeAll(allKeys)
                    }

                    launch {
                        context.prefManager.blacklistedItems = currentlyBlacklisted
                        context.writeSetting(SettingsType.SECURE, "icon_blacklist", currentlyBlacklisted.joinToString(","))
                    }

                    true
                }
            }

            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)

                if (isCustom) {
                    holder.itemView.setOnLongClickListener {
                        RoundedBottomSheetDialog(context).apply {
                            setTitle(R.string.icon_blacklist_remove_custom)
                            setMessage(R.string.icon_blacklist_remove_custom_desc)
                            setPositiveButton(
                                android.R.string.ok
                            ) { _, _ ->
                                context.prefManager.let {
                                    val new = it.customBlacklistItems
                                    new.remove(CustomBlacklistItemInfo(title.toString(), key))

                                    it.customBlacklistItems = new
                                }
                                dismiss()
                            }
                            setNegativeButton(android.R.string.cancel, null)
                            show()
                        }

                        true
                    }
                }
            }
        }
    }

    private fun buildCustomCategory(category: CollapsiblePreferenceCategoryNew) {
        category.removeAll()
        category.addPreference(CustomBlacklistAddPreference(requireContext(), null))
        requireContext().prefManager.customBlacklistItems
            .map { item ->
                category.createPref(
                    title = item.label,
                    key = item.key,
                    isCustom = true
                )
            }
    }

    class BackupRestorePreference(context: Context) : Preference(context),
        IColorPreference by ColorPreference(context, null) {
        init {
            layoutResource = R.layout.custom_preference
        }

        override fun onBindViewHolder(holder: PreferenceViewHolder) {
            super.onBindViewHolder(holder)
            bindVH(holder)
        }
    }
}
