package com.rw.tweaks.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.recyclerview.widget.RecyclerView
import com.rw.tweaks.R
import com.rw.tweaks.anim.PrefAnimator
import com.rw.tweaks.prefs.BlacklistPreference
import com.rw.tweaks.util.dpAsPx
import com.rw.tweaks.util.forEach
import com.rw.tweaks.util.parseAutoIconBlacklistSlots
import kotlinx.coroutines.*
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceCategory
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceFragment

@SuppressLint("RestrictedApi")
class IconBlacklistFragment : CollapsiblePreferenceFragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener, CoroutineScope by MainScope() {
    private val origExpansionStates = HashMap<String, Boolean>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_blacklist, rootKey)
        
        createCategory(R.string.category_icon_blacklist_general, "icon_blacklist_general") {
            it.createPref(R.string.icon_blacklist_airplane, key = "airplane")
            it.createPref(R.string.icon_blacklist_vpn, key = "vpn")
            it.createPref(R.string.icon_blacklist_volte, key = "volte", additionalKeys = arrayOf("ims_volte"))
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
            it.createPref(R.string.icon_blacklist_alarm, key = "alarm")
            it.createPref(R.string.icon_blacklist_zen, key = "zen")
            it.createPref(R.string.icon_blacklist_do_not_disturb, key = "do_not_disturb", additionalKeys = arrayOf("dnd"))
            it.createPref(R.string.icon_blacklist_managed_profile, key = "managed_profile")
            it.createPref(R.string.icon_blacklist_nfc, key = "nfc", additionalKeys = arrayOf("nfc_on"))
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
        }
        
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
            it.createPref(R.string.icon_blacklist_phone_signal_second_stub, key = "phone_signal_second_stub")
            it.createPref(R.string.icon_blacklist_toddler, key = "toddler")
            it.createPref(R.string.icon_blacklist_keyguard_wakeup, key = "keyguard_wakeup")
            it.createPref(R.string.icon_blacklist_safezone, key = "safezone")
            it.createPref(R.string.icon_blacklist_wimax, key = "wimax")
            it.createPref(R.string.icon_blacklist_smart_bonding, key = "smart_bonding")
            it.createPref(R.string.icon_blacklist_private_mode, key = "private_mode")
        }
        
        createCategory(R.string.category_icon_blacklist_auto, "icon_blacklist_auto", resources.getText(R.string.category_icon_blacklist_auto_desc), null).apply {
            onExpandChangeListener = {
                if (wrappedGroup.preferenceCount == 0 && it) {
                    launch {
                        //TODO: we need to add a permission flow for DUMP and PACKAGE_USAGE_STATS
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

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText, preferenceScreen)
        return true
    }

    override fun onClose(): Boolean {
        origExpansionStates.forEach { (t, u) ->
            findPreference<CollapsiblePreferenceCategory>(t)?.expanded = u
        }

        origExpansionStates.clear()
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    private fun filter(query: String?, group: PreferenceGroup) {
        group.forEach { _, child ->
            if (child is PreferenceGroup) {
                if (child is CollapsiblePreferenceCategory) {
                    if (!origExpansionStates.containsKey(child.key)) origExpansionStates[child.key] = child.expanded
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
        return query.isNullOrBlank() || pref.title.contains(query, true)
    }
    
    private fun createCategory(title: Int, key: String, initialSummary: CharSequence? = null, children: ((CollapsiblePreferenceCategory) -> Unit)?): CollapsiblePreferenceCategory {
        return CollapsiblePreferenceCategory(requireContext(), null).apply { 
            setTitle(title)
            this.key = key
            this.summary = initialSummary

            preferenceScreen.addPreference(this)
            children?.invoke(this)
        }
    }
    
    private fun CollapsiblePreferenceCategory.createPref(titleRes: Int = 0, title: String? = null, key: String, additionalKeys: Array<String> = arrayOf(), autoWriteKey: String? = null): BlacklistPreference {
        return BlacklistPreference(requireContext(), null).apply { 
            if (titleRes != 0) {
                setTitle(titleRes)
            } else {
                this.title = title
            }
            this.key = key
            this.addAdditionalKeys(additionalKeys.toList())
            this.autoWriteKey = autoWriteKey

            addPreference(this)

            order = Preference.DEFAULT_ORDER
        }
    }
}