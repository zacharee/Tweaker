package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SearchIndex
import com.zacharee1.systemuituner.util.forEach
import com.zacharee1.systemuituner.util.hasPreference
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchFragment : BasePrefFragment(), SearchView.OnQueryTextListener {
    var onItemClickListener: ((action: Int, key: String?) -> Unit)? = null

    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }

    override val limitSummary: Boolean = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)
        preferenceScreen.isOrderingAsAdded = false
    }

    fun onShow() {
        launch {
            searchIndex.load().await()

            preferenceScreen.removeAll()
            searchIndex.filter(null) {
                it.forEach { pref ->
                    preferenceScreen.addPreference(
                        SearchIndex.ActionedPreference.copy(requireContext(), pref)
                    )
                }
            }
            preferenceScreen.isOrderingAsAdded = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.search_bg)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference is SearchIndex.ActionedPreference) {
            onItemClickListener?.invoke(preference.action, preference.key)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (context != null) {
            searchIndex.filter(newText) {
                val toRemove = ArrayList<Preference>()

                preferenceScreen.forEach { _, child ->
                    if (!it.map { c -> c.key }.contains(child.key)) {
                        toRemove.add(child)
                    }
                }

                toRemove.forEach { pref ->
                    preferenceScreen.removePreferenceRecursively(pref.key)
                }

                it.forEach { pref ->
                    if (!preferenceScreen.hasPreference(pref.key)) {
                        preferenceScreen.addPreference(
                            SearchIndex.ActionedPreference.copy(requireContext(), pref)
                        )
                    } else {
                        preferenceScreen.findPreference<Preference>(pref.key)?.order = pref.order
                    }
                }
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
}