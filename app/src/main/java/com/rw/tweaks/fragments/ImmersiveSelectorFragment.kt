package com.rw.tweaks.fragments

import android.content.Context
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import com.rw.tweaks.R
import com.rw.tweaks.activities.ImmersiveListSelector
import com.rw.tweaks.data.LoadedAppInfo
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.forEach
import com.rw.tweaks.util.hasPreference
import java.util.*
import kotlin.collections.ArrayList

class ImmersiveSelectorFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_blank, rootKey)
        preferenceScreen.isOrderingAsAdded = false
    }

    override val widgetLayout: Int = R.layout.checkbox

    private val selectorActivity: ImmersiveListSelector
        get() = requireActivity() as ImmersiveListSelector

    private val checked: HashSet<String>
        get() = selectorActivity.checked

    private val origItems = ArrayList<LoadedAppInfo>()

    fun setItems(items: Collection<LoadedAppInfo>) {
        preferenceScreen.removeAll()
        origItems.clear()
        origItems.addAll(items)

        items.forEach {
            preferenceScreen.addPreference(construct(it))
        }
    }

    fun onFilter(query: String?) {
        val toRemove = ArrayList<LoadedAppPreference>()
        val toAdd = ArrayList<LoadedAppPreference>()

        preferenceScreen.forEach { index, child ->
            child as LoadedAppPreference

            if (!child.matchesQuery(query)) toRemove.add(child)
        }

        origItems.forEach {
            if (it.matchesQuery(query) && !preferenceScreen.hasPreference(it.packageName)) {
                toAdd.add(construct(it))
            }
        }

        toRemove.forEach {
            preferenceScreen.removePreference(it)
        }

        toAdd.forEach {
            preferenceScreen.addPreference(it)
        }
    }

    private fun construct(info: LoadedAppInfo): LoadedAppPreference {
        return LoadedAppPreference(
            requireContext(),
            info
        ) { key, isChecked ->
            if (isChecked) {
                checked.add(key)
            } else {
                checked.remove(key)
            }
        }
    }

    inner class LoadedAppPreference(
        context: Context,
        val info: LoadedAppInfo,
        val callback: (key: String, checked: Boolean) -> Unit
    ) : CheckBoxPreference(context), ISecurePreference by SecurePreference(context) {
        init {
            title = info.label
            summary = info.packageName
            key = info.packageName
            icon = info.icon
            isChecked = checked.contains(info.packageName)

            if (info.colorPrimary != 0) {
                iconColor = info.colorPrimary
            }
        }

        override fun isPersistent(): Boolean {
            return false
        }

        override fun setChecked(checked: Boolean) {
            super.setChecked(checked)

            callback(key, checked)
        }

        fun matchesQuery(query: String?): Boolean {
            return info.matchesQuery(query)
        }
    }
}