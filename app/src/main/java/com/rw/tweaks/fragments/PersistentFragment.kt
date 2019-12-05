package com.rw.tweaks.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.data.SearchIndex
import com.rw.tweaks.util.PersistentOption
import com.rw.tweaks.util.forEach
import com.rw.tweaks.util.hasPreference
import com.rw.tweaks.util.prefManager

class PersistentFragment : BasePrefFragment(), SearchView.OnQueryTextListener {
    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }
    private val persistent by lazy { requireContext().prefManager.persistentOptions }

    override val widgetLayout: Int = R.layout.checkbox

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)

        preferenceScreen.removeAll()
        searchIndex.filterPersistent(null) {
            it.forEach { pref ->
                preferenceScreen.addPreference(construct(pref))
            }
        }

        preferenceScreen.isOrderingAsAdded = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.search_bg)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
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
                    preferenceScreen.addPreference(
                        SearchIndex.PersistentPreference.copy(requireContext(), pref)
                    )
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
    }

    private fun construct(pref: SearchIndex.PersistentPreference): SearchIndex.PersistentPreference {
        return SearchIndex.PersistentPreference.copy(requireContext(), pref).apply {
            isChecked = persistent.map { item -> item.key }.containsAll(pref.keys)
            setOnPreferenceChangeListener { preference, newValue ->
                preference as SearchIndex.PersistentPreference
                if (newValue.toString().toBoolean()) {
                    persistent.addAll(preference.keys.map { PersistentOption(preference.type, it) })
                } else {
                    persistent.removeAll { item -> preference.keys.contains(item.key) }
                }

                true
            }
        }
    }
}