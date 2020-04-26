package com.zacharee1.systemuituner.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zacharee1.systemuituner.IManager
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.*

//TODO: something weird is going on here where some settings are overridden incorrectly when first enabled as persistent.
//TODO: Figure it out?
class Manager : Service(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "manager_service"
    }

    private val observer = Observer()

    override fun onBind(intent: Intent?): IBinder {
        return ManagerImpl()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PrefManager.PERSISTENT_OPTIONS -> {
                doInitialCheck()
                observer.register()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
        try {
            doInitialCheck()
        } catch (e: IllegalStateException) {}
        observer.register()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    resources.getString(R.string.manager_channel),
                    NotificationManager.IMPORTANCE_LOW
                )
            )

            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(resources.getText(R.string.app_name))
                .setContentText(resources.getText(R.string.tap_to_disable))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this, 100, getNotificationSettingsForChannel(
                            NOTIFICATION_CHANNEL_ID
                        ), 0
                    )
                )
                .build()

            startForeground(1000, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        observer.unregister()
        prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun doInitialCheck() {
        prefManager.persistentOptions.forEach { opt ->
            runComparison(opt.type, opt.key)
        }
    }

    private fun runComparison(type: SettingsType, key: String) {
        val handler = PersistenceHandlerRegistry.handlers.find { it.settingsKey == key }

        if (handler != null) {
            val prefValue = handler.getPreferenceValueAsString()

            if (!handler.compareValues()) {
                writeSetting(type, key, prefValue)
            }
        } else {
            val value = try {
                getSetting(type, key)
            } catch (e: IllegalStateException) {
                Log.e("SystemUITuner", "A persistent option has an undefined settings type. Please clear app data.", e)
            }
            val prefValue = prefManager.savedOptions.find { it.type == type && it.key == key }?.value

            if (value != prefValue) {
                writeSetting(type, key, prefValue)
            }
        }
    }

    inner class ManagerImpl : IManager.Stub() {

    }

    inner class Observer : ContentObserver(mainHandler) {
        fun register() {
            unregister()

            prefManager.persistentOptions.forEach {
                val uri = when (it.type) {
                    SettingsType.GLOBAL -> Settings.Global.getUriFor(it.key)
                    SettingsType.SECURE -> Settings.Secure.getUriFor(it.key)
                    SettingsType.SYSTEM -> Settings.System.getUriFor(it.key)
                    else -> return@forEach
                }

                contentResolver.registerContentObserver(uri, true, this@Observer)
            }
        }

        fun unregister() {
            contentResolver.unregisterContentObserver(this)
        }

        override fun onChange(selfChange: Boolean, uri: Uri) {
            val type = SettingsType.fromString(uri.pathSegments.run { this[lastIndex - 1] })
            val key = uri.lastPathSegment

            runComparison(type, key)
        }
    }
}