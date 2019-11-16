package com.rw.tweaks.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.rw.tweaks.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")
class SearchIndex private constructor(context: Context) : ContextWrapper(context), CoroutineScope by MainScope() {
    companion object {
        private var instance: SearchIndex? = null

        fun getInstance(context: Context): SearchIndex {
            return instance ?: kotlin.run {
                SearchIndex(context.applicationContext).apply { instance = this }
            }
        }
    }

    private val preferenceManager = PreferenceManager(this)
    private val preferences = ArrayList<ActionedPreference>()

    private var isLoaded = async {
        inflate(R.xml.prefs_apps, R.id.appsFragment)
        inflate(R.xml.prefs_developer, R.id.developerFragment)
        inflate(R.xml.prefs_display, R.id.displayFragment)
        inflate(R.xml.prefs_net_misc, R.id.netMiscellaneousFragment)
        inflate(R.xml.prefs_notifications, R.id.notificationsFragment)
        inflate(R.xml.prefs_storage, R.id.storageFragment)
        inflate(R.xml.prefs_ui, R.id.UIFragment)
        true
    }

    private fun inflate(resource: Int, action: Int): PreferenceScreen {
        return preferenceManager.inflateFromResource(this, resource, null).also { process(it, action) }
    }

    private fun process(group: PreferenceGroup, action: Int) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is PreferenceGroup) process(child, action)
            else preferences.add(
                ActionedPreference(this).apply {
                    title = child.title
                    summary = child.title
                    icon = child.icon
                    layoutResource = child.layoutResource
                    key = child.key
                    this.action = action
                }
            )
        }
    }

    fun filter(query: String?, result: (ArrayList<ActionedPreference>) -> Unit) = launch {
        val lowercase = query?.toLowerCase(Locale.getDefault())

        isLoaded.await()

        val filter = async {
            ArrayList(
                preferences.filter {
                    lowercase == null || lowercase.isBlank() ||
                            it.title.toString().toLowerCase(Locale.getDefault()).contains(lowercase) ||
                            it.summary.toString().toLowerCase(Locale.getDefault()).contains(lowercase)
                }
            )
        }

        result(filter.await())
    }

    class ActionedPreference(context: Context) : Preference(context) {
        var action: Int = 0
    }
}