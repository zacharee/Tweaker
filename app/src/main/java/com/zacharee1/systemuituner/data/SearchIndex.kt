package com.zacharee1.systemuituner.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.preference.*
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefManager
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO: Persistent Options were kind of just shoved in here. Clean this up.
 */
@SuppressLint("RestrictedApi")
class SearchIndex private constructor(context: Context) : ContextWrapper(context), CoroutineScope by MainScope(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private var instance: SearchIndex? = null

        val toInflate = arrayOf(
            R.xml.prefs_apps to R.id.appsFragment,
            R.xml.prefs_audio to R.id.audioFragment,
            R.xml.prefs_developer to R.id.developerFragment,
            R.xml.prefs_display to R.id.displayFragment,
            R.xml.prefs_net_cellular to R.id.netCellFragment,
            R.xml.prefs_net_misc to R.id.netMiscellaneousFragment,
            R.xml.prefs_net_wifi to R.id.netWiFiFragment,
            R.xml.prefs_notifications to R.id.notificationsFragment,
            R.xml.prefs_status_bar to R.id.statusBarFragment,
            R.xml.prefs_storage to R.id.storageFragment,
            R.xml.prefs_ui to R.id.UIFragment,
            R.xml.prefs_advanced to R.id.advancedFragment
        )

        fun getInstance(context: Context): SearchIndex {
            return instance ?: run {
                SearchIndex(context.applicationContext).apply { instance = this }
            }
        }
    }

    private val preferenceManager = PreferenceManager(this)
    private val preferences = ArrayList<ActionedPreference>()

    private var isLoaded = buildLoader()

    init {
        prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PrefManager.FORCE_ENABLE_ALL) {
            isLoaded = buildLoader()
        }
    }

    private fun buildLoader(): Deferred<Boolean> {
        return async {
            preferences.clear()
            toInflate.forEach {
                inflate(it.first, it.second)
            }
            true
        }
    }

    private fun inflate(resource: Int, action: Int): PreferenceScreen {
        return preferenceManager.inflateFromResource(this, resource, null).also { process(it, action) }
    }

    private fun process(group: PreferenceGroup, action: Int) {
        for (i in 0 until group.preferenceCount) {
            val child = group.getPreference(i)

            if (child is PreferenceGroup) process(child, action)
            else preferences.add(ActionedPreference.fromPreference(this, child, action))
        }
    }

    fun filter(query: String?, result: (ArrayList<ActionedPreference>) -> Unit) = launch {
        val lowercase = query?.toLowerCase(Locale.getDefault())

        isLoaded.await()

        val filter = async {
            ArrayList(
                preferences.filter {
                    lowercase == null || lowercase.isBlank() ||
                            it.title.toString().contains(lowercase, true) ||
                            it.summary.toString().contains(lowercase, true)
                }
            )
        }

        result(filter.await())
    }

    class ActionedPreference(context: Context) : Preference(context), ISecurePreference by SecurePreference(
        context,
        null
    ), ISpecificPreference, IColorPreference by ColorPreference(
        context,
        null
    ) {
        companion object {
            fun fromPreference(context: Context, preference: Preference, action: Int): ActionedPreference {
                return ActionedPreference(context).apply {
                    title = preference.title
                    summary = preference.summary
                    icon = preference.icon
                    key = preference.key
                    isVisible = preference.isVisible
                    if (preference is ISecurePreference) {
                        dangerous = preference.dangerous
                        type = preference.type
                    }
                    if (preference is ISpecificPreference) {
                        _keys.addAll(preference.keys)
                    }
                    if (preference is IColorPreference) {
                        iconColor = preference.iconColor
                    }
                    if (preference is IVerifierPreference) {
                        visibilityVerifier = preference.visibilityVerifier
                        init(this)
                    }
                    this.action = action
                }
            }

            fun copy(context: Context, preference: ActionedPreference): ActionedPreference {
                return fromPreference(context, preference, preference.action)
            }
        }

        val _keys = ArrayList<String>()

        override val keys: Array<String>
            get() = _keys.toTypedArray()

        var action: Int = 0

        override var dangerous: Boolean = false
            set(value) {
                field = value
                markDangerous()
            }

        init {
            layoutResource = R.layout.custom_preference
        }

        override fun onBindViewHolder(holder: PreferenceViewHolder) {
            super.onBindViewHolder(holder)

            bindVH(holder)
        }

        fun copy(): ActionedPreference {
            return fromPreference(context, this, action)
        }

        fun markDangerous() {
            title = if (dangerous) {
                SpannableString(title).apply {
                    setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
                }
            } else {
                title.toString()
            }
        }
    }
}