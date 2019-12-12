package com.rw.tweaks.services

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
import androidx.core.app.NotificationCompat
import com.rw.tweaks.IManager
import com.rw.tweaks.R
import com.rw.tweaks.util.*

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
            PrefManager.PERSISTENT_OPTIONS -> observer.register()
        }
    }

    override fun onCreate() {
        super.onCreate()

        observer.register()
        prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
        doInitialCheck()

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
        prefManager.persistentOptions.forEach {
            val value = when (it.type) {
                SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, it.key)
                SettingsType.SECURE -> Settings.Secure.getString(contentResolver, it.key)
                SettingsType.SYSTEM -> Settings.System.getString(contentResolver, it.key)
                else -> return@forEach
            }

            val prefValue = prefManager.prefs.all[it.key]?.toString()

            if (value != prefValue) {
                writeSetting(it.type, it.key, prefValue)
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
            val savedValue = prefManager.prefs.all[key]?.toString()
            val newValue = getSetting(type, key)

            if (savedValue != newValue) {
                writeSetting(type, key, savedValue)
            }
        }
    }
}