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
import android.os.UserHandle
import android.provider.Settings
import com.zacharee1.systemuituner.BuildConfig
import com.zacharee1.systemuituner.IShizukuOperationsService
import rikka.shizuku.Shizuku

@Suppress("unused")
class ShizukuOperationsService : IShizukuOperationsService.Stub {
    private val context: Context

    constructor() {
        @Suppress("INACCESSIBLE_TYPE")
        val context = (ActivityThread.systemMain().systemContext as Context)

        this.context = context.createPackageContextAsUser(
            BuildConfig.APPLICATION_ID,
            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UserHandle.of(Shizuku.getUid())
            } else {
                UserHandle(Shizuku.getUid())
            }
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
        return read(Settings.Global.CONTENT_URI, key)
    }

    override fun readSecure(key: String?): String? {
        return read(Settings.Secure.CONTENT_URI, key)
    }

    override fun readSystem(key: String?): String? {
        return read(Settings.System.CONTENT_URI, key)
    }

    override fun destroy() {}

    private fun write(uri: Uri, key: String?, value: String?, pkg: String?): Boolean {
        return try {
            uri.useProvider {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    insert(
                        AttributionSource(
                            Shizuku.getUid(),
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

    private fun read(uri: Uri, key: String?): String? {
        return uri.useProvider { read(uri, key) }
    }

    private fun resolveCallingPackage(): String? {
        return when (Shizuku.getUid()) {
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
                Shizuku.getUid(),
                token,
                "*cmd*"
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
                    Shizuku.getUid()
                )
            }
        }
    }
}