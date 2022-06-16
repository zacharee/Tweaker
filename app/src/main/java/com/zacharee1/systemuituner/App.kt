package com.zacharee1.systemuituner

import android.app.Application
import android.content.*
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.AndroidRuntimeException
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zacharee1.systemuituner.services.Manager
import com.zacharee1.systemuituner.util.PersistenceHandlerRegistry
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefManager
import org.lsposed.hiddenapibypass.HiddenApiBypass

class App : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        fun updateServiceState(context: Context) {
            with (context) {
                if (prefManager.persistentOptions.isEmpty()) {
                    try {
                        unbindService(connection)
                    } catch (_: IllegalArgumentException) {}
                    stopService(Intent(this, Manager::class.java))
                } else {
                    try {
                        bindService(Intent(this, Manager::class.java), connection, Context.BIND_AUTO_CREATE)
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

        initExceptionHandler()

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

    private fun initExceptionHandler() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if (e is AndroidRuntimeException) {
                Log.e("SystemUITuner", "Caught a runtime Exception!", e)
                FirebaseCrashlytics.getInstance().recordException(Exception("Caught a runtime Exception!", e))
                Looper.loop()
            } else {
                previousHandler?.uncaughtException(t, e)
            }
        }
    }
}