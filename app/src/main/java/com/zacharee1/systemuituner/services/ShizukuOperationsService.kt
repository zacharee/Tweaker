package com.zacharee1.systemuituner.services

import android.app.ActivityManager
import android.app.ActivityThread
import android.content.AttributionSource
import android.content.ContentValues
import android.content.Context
import android.content.IContentProvider
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.os.UserHandleCompat
import com.zacharee1.systemuituner.BuildConfig
import com.zacharee1.systemuituner.IShizukuOperationsService
import com.zacharee1.systemuituner.data.SettingsType
import rikka.shizuku.Shizuku

@Suppress("unused")
class ShizukuOperationsService : IShizukuOperationsService.Stub {
    private val contentResolver by lazy {
        @Suppress("INACCESSIBLE_TYPE")
        (ActivityThread.currentActivityThread().systemContext as Context).contentResolver
    }

    private val context: Context

    constructor() {
        @Suppress("INACCESSIBLE_TYPE")
        val context = (ActivityThread.systemMain().systemContext as Context)

        this.context = context.createPackageContextAsUser(
            BuildConfig.APPLICATION_ID,
            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
            UserHandleCompat.getUserHandleForUid(safeUid()),
        )
    }

    constructor(context: Context) {
        this.context = context
    }

    override fun writeGlobal(key: String?, value: String?, pkg: String?): Boolean {
        return write(Settings.Global.CONTENT_URI, key, value, pkg)
    }

    override fun writeSecure(key: String?, value: String?, pkg: String?): Boolean {
        return write(Settings.Secure.CONTENT_URI, key, value, pkg)
    }

    override fun writeSystem(key: String?, value: String?, pkg: String?): Boolean {
        return write(Settings.System.CONTENT_URI, key, value, pkg)
    }

    override fun readGlobal(key: String?): String? {
        val token = Binder.clearCallingIdentity()

        try {
            return Settings.Global.getString(contentResolver, key)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun readSecure(key: String?): String? {
        val token = Binder.clearCallingIdentity()

        try {
            return Settings.Secure.getString(contentResolver, key)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun readSystem(key: String?): String? {
        val token = Binder.clearCallingIdentity()

        try {
            return Settings.System.getString(contentResolver, key)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun destroy() {}

    private fun write(uri: Uri, key: String?, value: String?, pkg: String?): Boolean {
        return try {
            uri.useProvider {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    insert(
                        AttributionSource(
                            safeUid(),
                            resolveCallingPackage(),
                            null,
                        ),
                        uri,
                        ContentValues().apply {
                            put("name", key)
                            put("value", value)
                            put("package", pkg)
                        },
                        Bundle(),
                    )
                } else {
                    @Suppress("DEPRECATION")
                    insert(
                        resolveCallingPackage(),
                        uri,
                        ContentValues().apply {
                            put("name", key)
                            put("value", value)
                            put("package", pkg)
                        },
                    )
                }
            }
            true
        } catch (e: Exception) {
            false
        } finally {

        }
    }

    private fun read(which: SettingsType, key: String?): String? {
        val token = Binder.clearCallingIdentity()

        return try {
            when (which) {
                SettingsType.UNDEFINED -> throw IllegalArgumentException("Invalid settings type!")
                SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, key)
                SettingsType.SECURE -> Settings.Secure.getString(contentResolver, key)
                SettingsType.SYSTEM -> Settings.System.getString(contentResolver, key)
            }
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    private fun resolveCallingPackage(): String? {
        return when (safeUid()) {
            android.os.Process.ROOT_UID -> {
                "root"
            }

            android.os.Process.SHELL_UID -> {
                "com.android.shell"
            }

            else -> {
                null
            }
        }
    }

    private fun <T> Uri.useProvider(block: IContentProvider.() -> T?): T? {
        val activityManager = ActivityManager.getService()
        val token = Binder()

        var provider: IContentProvider? = null

        try {
            val holder = activityManager.getContentProviderExternal(
                authority,
                safeUid(),
                token,
                "*cmd*",
            ) ?: throw IllegalStateException("Could not find provider for ${authority}!")

            provider = holder.provider

            if (provider == null) {
                throw java.lang.IllegalStateException("Provider for $authority is null!")
            }

            return provider.block()
        } finally {
            provider?.also {
                activityManager.removeContentProviderExternalAsUser(
                    authority,
                    token,
                    safeUid(),
                )
            }
        }
    }

    private fun safeUid(): Int {
        return try {
            Shizuku.getUid()
        } catch (e: IllegalStateException) {
            2000
        }
    }
}