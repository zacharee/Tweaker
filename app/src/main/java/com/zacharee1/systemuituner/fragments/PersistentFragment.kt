package com.zacharee1.systemuituner.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroupAdapter
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.PersistentOption
import com.zacharee1.systemuituner.data.SearchIndex
import com.zacharee1.systemuituner.dialogs.CustomPersistentOptionDialogFragment
import com.zacharee1.systemuituner.util.*

class PersistentFragment : BasePrefFragment(), SearchView.OnQueryTextListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }
    private val persistent by lazy { requireContext().prefManager.persistentOptions }

    override val widgetLayout: Int = R.layout.checkbox

    private var currentQuery: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)

        preferenceScreen.removeAll()
        searchIndex.filterPersistent(null) {
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
        searchIndex.filterPersistent(newText) {
            val toRemove = ArrayList<Preference>()

            preferenceScreen.forEach { _, child ->
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

        requireContext().prefManager.persistentOptions = persistent
        requireContext().prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun addCustomItem() {
        val fragment = CustomPersistentOptionDialogFragment()
        fragment.setTargetFragment(this, 0)
        fragment.show(parentFragmentManager, null)
    }

    private fun construct(pref: SearchIndex.PersistentPreference): SearchIndex.PersistentPreference {
        return SearchIndex.PersistentPreference.copy(pref, requireActivity()).apply {
            isChecked = persistent.filter { it.type == type && keys.contains(it.key) }.size == keys.size
            setOnPreferenceChangeListener { preference, newValue ->
                preference as SearchIndex.PersistentPreference

                if (newValue.toString().toBoolean()) {
                    persistent.addAll(preference.keys.map {
                        PersistentOption(
                            preference.type,
                            it
                        )
                    })
                } else {
                    persistent.removeAll { item -> item.type == preference.type && preference.keys.contains(item.key) }
                }

                mainHandler.post {
                    (listView.adapter as PreferenceGroupAdapter?)?.updatePreferences()
                }

                true
            }
        }
    }
}