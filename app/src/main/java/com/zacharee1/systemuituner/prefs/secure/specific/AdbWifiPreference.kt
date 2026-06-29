package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.database.ContentObserver
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.SecureSwitchPreference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class AdbWifiPreference(context: Context, attrs: AttributeSet) :
    SecureSwitchPreference(context, attrs) {
    companion object {
        private const val ADB_SERVICE_TYPE = "_adb-tls-connect._tcp."
        private const val DISCOVERY_TIMEOUT_MILLIS = 15_000L
        private const val PORT_SCAN_ATTEMPTS = 3
        private const val PORT_SCAN_CONCURRENCY = 128
        private const val SOCKET_CONNECT_TIMEOUT_MILLIS = 50
        private const val SOCKET_READ_TIMEOUT_MILLIS = 500
        private val PORTS = listOf(5555) + (33000..47000)
    }

    private var scope: CoroutineScope? = null
    private var discoveryJob: Job? = null
    private val nsdManager by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }

    var discoveredPort: Int? = null
        private set(value) {
            field = value
            onPortDiscoveredListener?.invoke(value)
        }

    var onPortDiscoveredListener: ((Int?) -> Unit)? = null

    private val settingsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            updateState()
        }
    }

    override fun onAttached() {
        super.onAttached()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor("adb_wifi_enabled"),
            true,
            settingsObserver
        )
        updateState()
    }

    override fun onDetached() {
        try {
            context.contentResolver.unregisterContentObserver(settingsObserver)
        } catch (_: Exception) {}
        stopDiscovery()
        scope?.cancel()
        scope = null
        super.onDetached()
    }

    private fun updateState() {
        val isEnabled = Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0) != 0
        if (isEnabled) {
            startDiscovery()
        } else {
            stopDiscovery()
            summary = context.getString(R.string.feature_enable_wireless_adb_desc)
        }
    }

    @Suppress("DEPRECATION")
    private fun startDiscovery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        stopDiscovery()
        summary = context.getString(R.string.feature_enable_wireless_adb_searching_port)

        discoveryJob = scope?.launch {
            val portFound = CompletableDeferred<Int>()

            val multicastLock = try {
                val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifi.createMulticastLock("AdbWifiPreference").apply {
                    setReferenceCounted(false)
                    acquire()
                }
            } catch (_: Exception) {
                null
            }

            val discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {}
                override fun onDiscoveryStopped(serviceType: String) {}
                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
                        override fun onServiceResolved(resolvedServiceInfo: NsdServiceInfo) {
                            val resolvedHost = resolvedServiceInfo.host
                            val isLocal = resolvedHost?.isLoopbackAddress == true ||
                                    getLocalIpAddresses().any { it == resolvedHost }
                            if (isLocal) {
                                portFound.complete(resolvedServiceInfo.port)
                            }
                        }
                    })
                }
                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {}
                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {}
            }

            try {
                nsdManager.discoverServices(
                    ADB_SERVICE_TYPE,
                    NsdManager.PROTOCOL_DNS_SD,
                    discoveryListener
                )
            } catch (_: Exception) {}

            val portScanJob = launch(Dispatchers.IO) scan@{
                repeat(PORT_SCAN_ATTEMPTS) {
                    if (!isActive) return@scan
                    scanLocalAdbPort()?.let {
                        portFound.complete(it)
                        return@scan
                    }
                    delay(1000)
                }
            }

            try {
                val port = withTimeoutOrNull(DISCOVERY_TIMEOUT_MILLIS) {
                    portFound.await()
                }
                if (port != null) {
                    discoveredPort = port
                    summary = context.getString(
                        R.string.feature_enable_wireless_adb_desc_with_port,
                        port
                    )
                } else {
                    summary = context.getString(R.string.feature_enable_wireless_adb_port_not_found)
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                portScanJob.cancel()
                try { nsdManager.stopServiceDiscovery(discoveryListener) } catch (_: Exception) {}
                try { multicastLock?.release() } catch (_: Exception) {}
            }
        }
    }

    private fun stopDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = null
        discoveredPort = null
    }

    private fun getLocalIpAddresses(): List<InetAddress> {
        val addresses = mutableListOf<InetAddress>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    addresses.add(inetAddresses.nextElement())
                }
            }
        } catch (_: Exception) {}
        return addresses
    }

    private suspend fun scanLocalAdbPort(): Int? = coroutineScope {
        val targets = buildList {
            add("127.0.0.1")
            addAll(
                getLocalIpAddresses()
                    .filterIsInstance<Inet4Address>()
                    .mapNotNull { it.hostAddress }
            )
        }.distinct()

        PORTS.chunked(PORT_SCAN_CONCURRENCY).forEach { ports ->
            val discoveredPort = ports.map { port ->
                async(Dispatchers.IO) {
                    targets.firstNotNullOfOrNull { ip ->
                        if (isAdbPort(ip, port)) port else null
                    }
                }
            }.awaitAll().firstOrNull()

            if (discoveredPort != null) {
                return@coroutineScope discoveredPort
            }
        }
        null
    }

    private fun isAdbPort(ip: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(ip, port),
                    SOCKET_CONNECT_TIMEOUT_MILLIS
                )
                socket.soTimeout = SOCKET_READ_TIMEOUT_MILLIS
                val outputStream = socket.getOutputStream()
                outputStream.write(createAdbCnxnPacket())
                outputStream.flush()

                val inputStream = socket.getInputStream()
                val response = ByteArray(24)
                var offset = 0
                while (offset < response.size) {
                    val read = inputStream.read(response, offset, response.size - offset)
                    if (read < 0) {
                        return false
                    }
                    offset += read
                }

                when (ByteBuffer.wrap(response).order(ByteOrder.LITTLE_ENDIAN).getInt()) {
                    0x4E584E43, // CNXN
                    0x48545541, // AUTH
                    0x534C5453, // STLS
                    -> true
                    else -> false
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun createAdbCnxnPacket(): ByteArray {
        val data = "host::\u0000".toByteArray(StandardCharsets.US_ASCII)
        val checksum = data.sumOf { it.toInt() and 0xFF }
        return ByteBuffer.allocate(24 + data.size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .apply {
                putInt(0x4E584E43) // CNXN
                putInt(0x01000000) // Version 1.0
                putInt(256 * 1024) // Max data
                putInt(data.size)
                putInt(checksum)
                putInt(0x4E584E43.inv())
                put(data)
            }
            .array()
    }
}
