package com.rw.tweaks.fragments

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.data.SearchIndex

class SearchFragment : PreferenceFragmentCompat(), SearchView.OnQueryTextListener {
    var onItemClickListener: ((action: Int, key: String?) -> Unit)? = null

    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)

        preferenceScreen.removeAll()
        searchIndex.filter(null) {
            it.forEach { pref ->
                preferenceScreen.addPreference(
                    SearchIndex.ActionedPreference(requireContext()).apply {
                        key = pref.key
                        title = pref.title
                        summary = pref.summary
                        action = pref.action
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.search_bg)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference is SearchIndex.ActionedPreference) {
            onItemClickListener?.invoke(preference.action, preference.key)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchIndex.filter(newText) {
            it.forEach { pref ->
                preferenceScreen.addPreference(pref)
            }
        }
        preferenceScreen.removeAll()

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
}