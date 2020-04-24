package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroupAdapter
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.PersistentOption
import com.zacharee1.systemuituner.data.SearchIndex
import com.zacharee1.systemuituner.util.*

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
    }

    private fun construct(pref: SearchIndex.PersistentPreference): SearchIndex.PersistentPreference {
        return SearchIndex.PersistentPreference.copy(requireContext(), pref).apply {
            isChecked = persistent.map { item -> item.key }.containsAll(pref.keys)
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
                    persistent.removeAll { item -> preference.keys.contains(item.key) }
                }

                mainHandler.post {
                    (listView.adapter as PreferenceGroupAdapter?)?.updatePreferences()
                }

                true
            }
        }
    }
}