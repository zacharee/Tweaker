package com.rw.tweaks.fragments

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.data.SearchIndex

class SearchFragment : PreferenceFragmentCompat(), SearchView.OnQueryTextListener {
    var onItemClickListener: (() -> Unit)? = null

    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)

        preferenceScreen.removeAll()
        searchIndex.filter(null).forEach {
            preferenceScreen.addPreference(it)
        }
    }

    fun show(fragmentManager: FragmentManager, tag: String?) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .add(R.id.nav_host_fragment, this, tag)
            .addToBackStack("search")
            .commit()
    }

    fun dismiss(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .remove(this)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.search_bg)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference is SearchIndex.ActionedPreference) {
            onItemClickListener?.invoke()

            requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(preference.action)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val display = searchIndex.filter(newText)
        preferenceScreen.removeAll()

        display.forEach {
            preferenceScreen.addPreference(it)
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
}