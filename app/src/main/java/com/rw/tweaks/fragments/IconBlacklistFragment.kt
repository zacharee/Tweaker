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
import com.rw.tweaks.util.dpAsPx
import com.rw.tweaks.util.forEach
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceCategory
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceFragment

@SuppressLint("RestrictedApi")
class IconBlacklistFragment : CollapsiblePreferenceFragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private val origExpansionStates = HashMap<String, Boolean>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_blacklist, rootKey)
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
}