package com.zacharee1.systemuituner

import android.app.Application
import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.zacharee1.systemuituner.services.Manager
import com.zacharee1.systemuituner.util.PersistenceHandlerRegistry
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefManager
import org.lsposed.hiddenapibypass.HiddenApiBypass

class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        fun updateServiceState(context: Context) {
            with (context) {
                if (prefManager.persistentOptions.isEmpty()) {
                    stopService(Intent(this, Manager::class.java))
                } else {
                    try {
                        ContextCompat.startForegroundService(this, Intent(this, Manager::class.java))
                    } catch (e: Exception) {
                        Log.e("SystemUITuner", "Unable to start foreground service. Build SDK ${Build.VERSION.SDK_INT}.", e)
                        FirebaseCrashlytics.getInstance().apply {
                            recordException(Exception("Unable to start foreground service. Build SDK ${Build.VERSION.SDK_INT}", e))
                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("L")
        }
        PersistenceHandlerRegistry.register(this)

        updateServiceState(this)
        prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PrefManager.PERSISTENT_OPTIONS -> {
                updateServiceState(this)
            }
        }
    }
}