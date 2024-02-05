package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.zacharee1.systemuituner.BuildConfig
import com.zacharee1.systemuituner.IShizukuOperationsService
import com.zacharee1.systemuituner.services.ShizukuOperationsService
import rikka.shizuku.Shizuku
import java.util.concurrent.ConcurrentLinkedQueue

val Context.shizukuServiceManager: ShizukuServiceManager
    get() = ShizukuServiceManager.getInstance(this)

class ShizukuServiceManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ShizukuServiceManager? = null

        @Synchronized
        fun getInstance(context: Context): ShizukuServiceManager {
            return instance ?: ShizukuServiceManager(context.applicationContext ?: context).apply {
                instance = this
            }
        }
    }

    private var service: IShizukuOperationsService? = null

    private val commandQueue = ConcurrentLinkedQueue<IShizukuOperationsService.() -> Unit>()

    private val shizukuServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@ShizukuServiceManager.service = IShizukuOperationsService.Stub.asInterface(service)
            onServiceConnected()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            this@ShizukuServiceManager.service = null
        }
    }

    val isShizukuInstalled: Boolean
        get() = try {
            packageManager.getApplicationInfoCompat("moe.shizuku.privileged.api")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    private val shizukuServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            ShizukuOperationsService::class.java.name
        )
    )
        .daemon(false)
        .processNameSuffix(":service")
        .debuggable(true)
        .version(BuildConfig.VERSION_CODE)

    fun onCreate() {
        Shizuku.addBinderReceivedListenerSticky {
            if (hasShizukuPermission) {
                Shizuku.bindUserService(
                    shizukuServiceArgs,
                    shizukuServiceConnection
                )
            }
        }

        Shizuku.addRequestPermissionResultListener { _, _ ->
            if (hasShizukuPermission) {
                Shizuku.bindUserService(
                    shizukuServiceArgs,
                    shizukuServiceConnection
                )
            }
        }

        Shizuku.addBinderDeadListener {
            this.service = null
        }
    }

    fun enqueue(block: IShizukuOperationsService.() -> Unit) {
        val service = service

        if (service != null) {
            service.block()
        } else {
            commandQueue.add(block)
        }
    }

    fun waitForService(): IShizukuOperationsService {
        @Suppress("ControlFlowWithEmptyBody")
        while (service == null);

        return service!!
    }

    private fun onServiceConnected() {
        while (commandQueue.isNotEmpty()) {
            val command = commandQueue.remove()

            service?.command()
        }
    }
}