package com.rw.tweaks

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.rw.tweaks.services.Manager
import com.rw.tweaks.util.PrefManager
import com.rw.tweaks.util.prefManager
import tk.zwander.unblacklister.disableApiBlacklist

class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate() {
        super.onCreate()

        disableApiBlacklist()

        updateServiceState()
        prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PrefManager.PERSISTENT_OPTIONS -> {
                updateServiceState()
            }
        }
    }

    private fun updateServiceState() {
        if (prefManager.persistentOptions.isEmpty()) {
            stopService(Intent(this, Manager::class.java))
        } else {
            ContextCompat.startForegroundService(this, Intent(this, Manager::class.java))
        }
    }
}