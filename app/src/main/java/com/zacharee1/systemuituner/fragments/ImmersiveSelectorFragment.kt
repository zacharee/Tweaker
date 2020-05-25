package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.ImmersiveListSelector
import com.zacharee1.systemuituner.data.LoadedAppInfo
import com.zacharee1.systemuituner.interfaces.ColorPreference
import com.zacharee1.systemuituner.interfaces.IColorPreference
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ImmersiveSelectorFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_blank, rootKey)
        preferenceScreen.isOrderingAsAdded = false
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return CustomPreferenceGroupAdapter(preferenceScreen)
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
        async {
            val toRemove = ArrayList<LoadedAppPreference>()
            val toAdd = ArrayList<LoadedAppPreference>()

            preferenceScreen.forEach { _, child ->
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
    }

    @SuppressLint("RestrictedApi")
    private fun construct(info: LoadedAppInfo): LoadedAppPreference {
        return LoadedAppPreference(
            requireContext(),
            info
        ) { key, isChecked, _ ->
            if (isChecked) {
                checked.add(key)
            } else {
                checked.remove(key)
            }

            async {
                (listView.adapter as CustomPreferenceGroupAdapter?)?.updatePreferences()
            }
        }
    }

    inner class LoadedAppPreference(
        context: Context,
        val info: LoadedAppInfo,
        val callback: (key: String, checked: Boolean, pref: Preference) -> Unit
    ) : CheckBoxPreference(context), IColorPreference by ColorPreference(
        context,
        null
    ) {
        init {
            layoutResource = R.layout.custom_preference
            widgetLayoutResource = R.layout.checkbox
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

            launch {
                callback(key, checked, this@LoadedAppPreference)
            }
        }

        override fun compareTo(other: Preference): Int {
            val sup = super.compareTo(other)

            return if (other is TwoStatePreference) {
                if (isChecked && !other.isChecked) -1
                else if (isChecked && other.isChecked) sup
                else if (!isChecked && other.isChecked) 1
                else sup
            } else sup
        }

        override fun onBindViewHolder(holder: PreferenceViewHolder) {
            super.onBindViewHolder(holder)

            bindVH(holder)
        }

        fun matchesQuery(query: String?): Boolean {
            return info.matchesQuery(query)
        }
    }

    @SuppressLint("RestrictedApi")
    inner class CustomPreferenceGroupAdapter(preferenceGroup: PreferenceGroup?) : PreferenceGroupAdapter(preferenceGroup) {
    }
}