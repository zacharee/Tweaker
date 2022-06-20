package com.zacharee1.systemuituner.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.preference.*
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("RestrictedApi")
class SearchIndex private constructor(context: Context) : ContextWrapper(context), CoroutineScope by MainScope() {
    companion object {
        @SuppressLint("StaticFieldLeak")
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
            R.xml.prefs_advanced to R.id.advancedFragment,
            R.xml.prefs_qs to R.id.qsFragment
        )

        fun getInstance(context: Context): SearchIndex {
            return instance ?: run {
                SearchIndex(context.applicationContext).apply { instance = this }
            }
        }
    }

    private val preferenceManager = PreferenceManager(this)
    private val preferences = ArrayList<ActionedPreference>()

    init {
        launch {
            load().await()
        }
    }

    fun load(): Deferred<Unit> {
        return async {
            preferences.clear()
            toInflate.forEach {
                inflate(it.first, it.second)
            }
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

    fun filter(query: String?, result: (Collection<ActionedPreference>) -> Unit) = launch {
        val filter = async {
            TreeSet(
                Comparator<ActionedPreference> { o1, o2 ->
                    if (query.isNullOrBlank()) {
                        o1.title.toString().compareTo(o2.title.toString(), true)
                    } else {
                        val o1Title = o1.title?.contains(query, true) == true
                        val o2Title = o2.title?.contains(query, true) == true

                        when {
                            o1Title && !o2Title -> -1
                            !o1Title && o2Title -> 1
                            else -> o1.title.toString().compareTo(o2.title.toString(), true)
                        }
                    }
                }
            ).apply {
                addAll(
                    preferences.filter {
                        query.isNullOrBlank() ||
                                it.title.toString().contains(query, true) ||
                                it.summary.toString().contains(query, true)
                    }
                )

                forEachIndexed { index, pref ->
                    pref.order = index
                }
            }
        }

        result(filter.await())
    }

    class ActionedPreference(context: Context) : Preference(context), ISecurePreference by SecurePreference(
        context,
        null
    ), ISpecificPreference, IColorPreference by ColorPreference(
        context,
        null
    ), IVerifierPreference by VerifierPreference(context, null) {
        companion object {
            fun fromPreference(context: Context, preference: Preference, action: Int): ActionedPreference {
                return ActionedPreference(context).apply {
                    title = preference.title
                    summary = preference.summary
                    icon = preference.icon
                    key = preference.key
                    order = preference.order
                    if (preference is IDangerousPreference) {
                        dangerous = preference.dangerous
                    }
                    if (preference is ISecurePreference) {
                        type = preference.type
                    }
                    if (preference is ISpecificPreference) {
                        _keys.putAll(preference.keys)
                    }
                    if (preference is IColorPreference) {
                        iconColor = preference.iconColor
                    }
                    if (preference is IVerifierPreference) {
                        visibilityVerifier = preference.visibilityVerifier
                    }
                    this.action = action

                    initSecure(this)
                    initVerify(this)
                }
            }

            fun copy(context: Context, preference: ActionedPreference): ActionedPreference {
                return fromPreference(context, preference, preference.action)
            }
        }

        val _keys = HashMap<SettingsType, Array<String>>()

        override val keys: HashMap<SettingsType, Array<String>>
            get() = _keys

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