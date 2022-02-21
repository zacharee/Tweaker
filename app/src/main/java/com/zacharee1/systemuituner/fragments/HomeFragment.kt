package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroupAdapter
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.App
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.nav.NavigationPreference
import com.zacharee1.systemuituner.util.updateTitle

class HomeFragment : BasePrefFragment() {
    override val supportsGrid = false

    private var selectedId: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_home, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.home)

        App.updateServiceState(requireContext())
    }

    @SuppressLint("RestrictedApi")
    private fun setSelection(id: String) {
        val oldId = selectedId
        selectedId = id

        val adapter = listView?.adapter as? PreferenceGroupAdapter

        adapter?.apply {
            oldId?.let {
                notifyItemChanged(getPreferenceAdapterPosition(it))
            }
            notifyItemChanged(getPreferenceAdapterPosition(id))
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference is NavigationPreference) {
            setSelection(preference.key)
        }

        return super.onPreferenceTreeClick(preference)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int, preference: Preference?) {
        holder.itemView.foreground = if (preference?.key == selectedId) ColorDrawable(
            ResourcesCompat.getColor(resources, R.color.selected_item, requireContext().theme)) else null
    }
}