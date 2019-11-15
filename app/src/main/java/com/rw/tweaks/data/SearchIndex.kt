package com.rw.tweaks.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.rw.tweaks.R
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")
class SearchIndex private constructor(context: Context) : ContextWrapper(context) {
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

    val apps = inflate(R.xml.prefs_apps, R.id.appsFragment)
    val developer = inflate(R.xml.prefs_developer, R.id.developerFragment)
    val display = inflate(R.xml.prefs_display, R.id.displayFragment)
    val netMisc = inflate(R.xml.prefs_net_misc, R.id.netMiscellaneousFragment)
    val notifications = inflate(R.xml.prefs_notifications, R.id.notificationsFragment)
    val storage = inflate(R.xml.prefs_storage, R.id.storageFragment)
    val ui = inflate(R.xml.prefs_ui, R.id.UIFragment)

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

    fun filter(query: String?): ArrayList<ActionedPreference> {
        val lowercase = query?.toLowerCase(Locale.getDefault())

        return ArrayList(
            preferences.filter {
                lowercase == null || lowercase.isBlank() || it.title.contains(lowercase) || it.summary.contains(lowercase)
            }
        )
    }

    class ActionedPreference(context: Context) : Preference(context) {
        var action: Int = 0
    }
}