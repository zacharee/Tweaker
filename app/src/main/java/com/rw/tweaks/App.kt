package com.rw.tweaks

import android.app.Application
import tk.zwander.unblacklister.disableApiBlacklist

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        disableApiBlacklist()
    }
}