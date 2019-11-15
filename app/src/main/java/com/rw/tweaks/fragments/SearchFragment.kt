package com.rw.tweaks.fragments

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.data.SearchIndex

class SearchFragment : PreferenceFragmentCompat(), SearchView.OnQueryTextListener {
    private val searchIndex by lazy { SearchIndex.getInstance(requireContext()) }
    private var searchView: SearchView? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_search, rootKey)
    }

    fun show(fragmentManager: FragmentManager, tag: String?) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.search_holder, this, tag)
            .commit()
    }

    fun dismiss(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .remove(this)
            .commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        searchView = menu.findItem(R.id.search).actionView as SearchView?

        searchView?.setOnQueryTextListener(this)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference is SearchIndex.ActionedPreference) {
            requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(preference.action)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onDestroy() {
        super.onDestroy()

        searchView?.setOnQueryTextListener(null)
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