@file:Suppress("DEPRECATION")

package com.zacharee1.systemuituner.services

import android.app.ActivityManagerNative
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
import android.system.Os
import android.util.Log
import com.zacharee1.systemuituner.IShizukuOperationsService
import com.zacharee1.systemuituner.data.SavedOption
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.systemsettingsaddon.library.listInternal
import kotlin.system.exitProcess

@Suppress("unused")
class ShizukuOperationsService(private val context: Context) : IShizukuOperationsService.Stub() {
    private val contentResolver by lazy {
        @Suppress("INACCESSIBLE_TYPE")
        (ActivityThread.currentActivityThread().systemContext as Context).contentResolver
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
        return read(SettingsType.GLOBAL, key)
    }

    override fun readSecure(key: String?): String? {
        return read(SettingsType.SECURE, key)
    }

    override fun readSystem(key: String?): String? {
        return read(SettingsType.SYSTEM, key)
    }

    override fun listSettings(): Array<SavedOption> {
        return list(SettingsType.GLOBAL) + list(SettingsType.SECURE) + list(SettingsType.SYSTEM)
    }

    override fun destroy() {
        exitProcess(0)
    }

    private fun write(uri: Uri, key: String?, value: String?, pkg: String?): Boolean {
        return uri.useProvider {
            try {
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
                } != null
            } catch (e: Throwable) {
                Log.e("SystemUITuner", "Error", e)
                throw e
            }
        }
    }

    private fun read(which: SettingsType, key: String?): String? {
        val token = Binder.clearCallingIdentity()

        return try {
            when (which) {
                SettingsType.UNDEFINED -> throw IllegalStateException("Invalid settings type!")
                SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, key)
                SettingsType.SECURE -> Settings.Secure.getString(contentResolver, key)
                SettingsType.SYSTEM -> Settings.System.getString(contentResolver, key)
            }
        } catch (e: Throwable) {
            try {
                which.contentUri.useProvider {
                    val method = which.callGetMethod
                    val args = Bundle().apply {
                        putInt(Settings.CALL_METHOD_USER_KEY, safeUid())
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        call(
                            AttributionSource(
                                safeUid(),
                                resolveCallingPackage(),
                                null,
                            ),
                            Settings.AUTHORITY,
                            method,
                            key,
                            args,
                        )
                    } else {
                        this::class.java.getMethod(
                            "call",
                            String::class.java,
                            String::class.java,
                            String::class.java,
                            Bundle::class.java,
                        ).invoke(
                            this,
                            resolveCallingPackage(),
                            Settings.AUTHORITY,
                            method,
                            key,
                            args,
                        ) as? Bundle?
                    }?.pairValue
                }
            } catch (e: Throwable) {
                throw e
            }
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    private fun list(which: SettingsType): Array<SavedOption> {
        return which.contentUri.useProvider {
            listInternal(which, resolveCallingPackage(), safeUid(), this)
                .map { SavedOption(it.type, it.key, it.value) }
                .toTypedArray()
        }
    }

    private fun resolveCallingPackage(): String? {
        return context.packageManager.getPackagesForUid(safeUid())?.getOrNull(0)
    }

    private fun <T> Uri.useProvider(block: IContentProvider.() -> T): T {
        val caller = Binder.clearCallingIdentity()
        val activityService = ActivityManagerNative.getDefault()
        var provider: IContentProvider? = null
        val token = Binder()

        try {
            provider = activityService.getContentProviderExternal(
                this.authority,
                UserHandle.USER_SYSTEM,
                token,
                "*cmd*",
            )?.provider

            return provider?.block()
                ?: throw IllegalStateException("Unable to acquire Content Provider ${authority}!")
        } catch (e: Throwable) {
            Log.e("SystemUITuner", "Error", e)
            throw e
        } finally {
            provider?.also {
                activityService.removeContentProviderExternal(
                    this.authority, token,
                )
            }
            Binder.restoreCallingIdentity(caller)
        }
    }

    private fun safeUid(): Int {
        return try {
            Os.getuid()
        } catch (e: IllegalStateException) {
            2000
        }
    }
}