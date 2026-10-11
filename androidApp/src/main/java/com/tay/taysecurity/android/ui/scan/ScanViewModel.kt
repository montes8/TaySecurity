package com.tay.taysecurity.android.ui.scan

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.NetworkInterface
import java.net.Socket
import java.util.Collections
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

data class DeviceScanResult(
    val ip: String,
    val hostName: String,
    val deviceType: String,
    val isSelf: Boolean = false,
    val isGateway: Boolean = false
)

data class ScanUiState(
    val isScanning: Boolean = false,
    val progress: Float = 0f,
    val scannedCount: Int = 0,
    val totalCount: Int = 254,
    val wifiName: String = "",
    val localIp: String = "",
    val gatewayIp: String = "",
    val isWifiConnected: Boolean = false,
    val devices: List<DeviceScanResult> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val context: Application
) : BaseViewModel() {

    var uiState by mutableStateOf(ScanUiState())
        private set

    init {
        checkWifiConnection()
    }

    fun checkWifiConnection() {
        @Suppress("DEPRECATION")
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        @Suppress("DEPRECATION")
        val dhcpInfo = wifiManager?.dhcpInfo

        if (wifiManager == null || dhcpInfo == null || dhcpInfo.ipAddress == 0) {
            uiState = uiState.copy(
                isWifiConnected = false,
                errorMessage = "No estás conectado a una red Wi-Fi."
            )
            return
        }

        val localIp = formatIpAddress(dhcpInfo.ipAddress)
        val gatewayIp = formatIpAddress(dhcpInfo.gateway)

        @Suppress("DEPRECATION")
        var ssid = wifiManager.connectionInfo?.ssid?.replace("\"", "") ?: "Red Wi-Fi"
        if (ssid == "<unknown ssid>" || ssid.isBlank()) {
            ssid = "Red Wi-Fi Local"
        }

        uiState = uiState.copy(
            isWifiConnected = true,
            wifiName = ssid,
            localIp = localIp,
            gatewayIp = gatewayIp,
            errorMessage = null
        )
    }

    fun startScan() {
        if (uiState.isScanning) return
        checkWifiConnection()

        if (!uiState.isWifiConnected) return

        @Suppress("DEPRECATION")
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        @Suppress("DEPRECATION")
        val dhcpInfo = wifiManager?.dhcpInfo ?: return

        val localIp = uiState.localIp
        val gatewayIp = uiState.gatewayIp

        Log.d("ScanDebug", "Iniciando escaneo. Local IP: $localIp, Gateway IP: $gatewayIp")

        // 1. Calcular rango exacto de las subredes
        val targetIps = LinkedHashSet<String>()
        val calculatedIps = getSubnetIpRange(dhcpInfo.ipAddress, dhcpInfo.netmask)
        targetIps.addAll(calculatedIps)

        val localParts = localIp.split(".")
        if (localParts.size == 4) {
            val localSubnet = "${localParts[0]}.${localParts[1]}.${localParts[2]}"
            for (i in 1..254) {
                targetIps.add("$localSubnet.$i")
            }
        }

        val gatewayParts = gatewayIp.split(".")
        if (gatewayParts.size == 4) {
            val gatewaySubnet = "${gatewayParts[0]}.${gatewayParts[1]}.${gatewayParts[2]}"
            for (i in 1..254) {
                targetIps.add("$gatewaySubnet.$i")
            }
        }

        val arpIps = getKernelArpTable()
        targetIps.addAll(arpIps)

        val totalTargets = targetIps.size
        val nsdDevices = ConcurrentHashMap<String, String>()

        execute {
            withContext(Dispatchers.Main) {
                uiState = uiState.copy(
                    isScanning = true,
                    progress = 0f,
                    scannedCount = 0,
                    totalCount = totalTargets,
                    devices = emptyList()
                )
            }

            @Suppress("DEPRECATION")
            val multicastLock = wifiManager.createMulticastLock("TaySecurityScanMulticastLock")?.apply {
                setReferenceCounted(false)
                acquire()
            }

            val nsdListeners = startNsdDiscovery(context, nsdDevices)

            try {
                coroutineScope {
                    launch(Dispatchers.IO) {
                        listenMdnsResponses(nsdDevices, 3500)
                    }
                    launch(Dispatchers.IO) {
                        resolveAppleLocalHostnames(nsdDevices)
                    }

                    if (localParts.size == 4) sendSubnetBroadcast("${localParts[0]}.${localParts[1]}.${localParts[2]}")
                    if (gatewayParts.size == 4) sendSubnetBroadcast("${gatewayParts[0]}.${gatewayParts[1]}.${gatewayParts[2]}")
                    sendSubnetBroadcast("192.168.18")

                    val semaphore = Semaphore(35)
                    val scannedCounter = AtomicInteger(0)
                    val foundDevices = Collections.synchronizedList(mutableListOf<DeviceScanResult>())

                    val jobs = targetIps.map { ip ->
                        async(Dispatchers.IO) {
                            semaphore.withPermit {
                                sendUdpProbes(ip)
                                val result = scanAddress(ip, localIp, gatewayIp, nsdDevices)
                                if (result != null) {
                                    foundDevices.add(result)
                                    Log.d("ScanDebug", "Dispositivo encontrado: IP=$ip, Nombre=${result.hostName}, Tipo=${result.deviceType}")
                                }
                                val count = scannedCounter.incrementAndGet()
                                val currentList = foundDevices.toList().sortedWith(
                                    compareByDescending<DeviceScanResult> { it.isGateway }
                                        .thenByDescending { it.isSelf }
                                        .thenBy { ipToLong(it.ip) }
                                )

                                withContext(Dispatchers.Main) {
                                    uiState = uiState.copy(
                                        scannedCount = count,
                                        progress = if (totalTargets > 0) count / totalTargets.toFloat() else 1f,
                                        devices = currentList
                                    )
                                }
                            }
                        }
                    }
                    jobs.awaitAll()
                }
            } finally {
                stopNsdDiscovery(context, nsdListeners)
                try {
                    multicastLock?.release()
                } catch (_: Exception) {}
            }

            withContext(Dispatchers.Main) {
                uiState = uiState.copy(
                    isScanning = false,
                    progress = 1f,
                    scannedCount = totalTargets
                )
            }
        }
    }

    private suspend fun scanAddress(
        ip: String,
        localIp: String,
        gatewayIp: String,
        nsdDevices: ConcurrentHashMap<String, String>
    ): DeviceScanResult? = withContext(Dispatchers.IO) {
        if (ip == localIp) {
            return@withContext DeviceScanResult(
                ip = ip,
                hostName = getHostName(ip) ?: "Este dispositivo",
                deviceType = "Tu Smartphone",
                isSelf = true
            )
        }

        // 1. Enviar sondas UDP para forzar la actualización de la tabla de vecinos ARP
        sendUdpProbes(ip)

        var reachable = false
        var discoveredHostName: String? = nsdDevices[ip]

        if (discoveredHostName != null) {
            reachable = true
        }

        // 2. Consulta mDNS Unicast directa con socket.receive() para capturar respuestas de Apple / Mac
        if (!reachable) {
            val unicastName = queryDeviceMdnsUnicast(ip, 350)
            if (unicastName != null) {
                discoveredHostName = unicastName
                reachable = true
            }
        }

        // 3. PING del sistema (ICMP nativo de Android)
        if (!reachable) {
            if (pingAddress(ip)) {
                reachable = true
            }
        }

        // 4. Consulta directa a la tabla de vecinos ARP de Android para esta IP
        if (!reachable) {
            if (checkIpInArpTable(ip)) {
                reachable = true
            }
        }

        // 5. Probar isReachable enlazando la interfaz de red activa (wlan0)
        if (!reachable) {
            reachable = isDeviceReachable(ip, localIp)
        }

        // 6. Probar puertos TCP principales (Windows SMB/WSD/RPC 445/139/135/5357/3389, AirPlay 5000/7000, Mac 631/5900/22, Web 80/443/8080/3000/5001/8000, iPhones 62078, Android 8008/5555)
        if (!reachable) {
            reachable = tryPort(ip, 445, 200) ||
                    tryPort(ip, 139, 200) ||
                    tryPort(ip, 135, 200) ||
                    tryPort(ip, 5357, 200) ||
                    tryPort(ip, 3389, 200) ||
                    tryPort(ip, 5000, 200) ||
                    tryPort(ip, 7000, 200) ||
                    tryPort(ip, 80, 200) ||
                    tryPort(ip, 443, 200) ||
                    tryPort(ip, 631, 200) ||
                    tryPort(ip, 5900, 200) ||
                    tryPort(ip, 22, 200) ||
                    tryPort(ip, 53, 200) ||
                    tryPort(ip, 8080, 200) ||
                    tryPort(ip, 62078, 200) ||
                    tryPort(ip, 8008, 200) ||
                    tryPort(ip, 3000, 200) ||
                    tryPort(ip, 5001, 200) ||
                    tryPort(ip, 8000, 200) ||
                    tryPort(ip, 49152, 200) ||
                    tryPort(ip, 49153, 200)
        }

        if (reachable || ip == gatewayIp) {
            var rawHostName = discoveredHostName ?: getHostName(ip)
            if (rawHostName == null || rawHostName == ip) {
                rawHostName = queryNetBiosName(ip)
            }
            if (rawHostName == null || rawHostName == ip) {
                rawHostName = queryDeviceMdnsUnicast(ip, 300)
            }
            if (rawHostName == null || rawHostName == ip) {
                rawHostName = queryHttpBanner(ip)
            }

            val defaultName = if (ip == gatewayIp) "Router Wi-Fi" else "Dispositivo de red"
            val hostName = rawHostName ?: defaultName
            val type = determineDeviceType(ip, gatewayIp, hostName)

            return@withContext DeviceScanResult(
                ip = ip,
                hostName = hostName,
                deviceType = type,
                isGateway = ip == gatewayIp
            )
        }

        return@withContext null
    }

    private fun checkIpInArpTable(targetIp: String): Boolean {
        // 1. Comando ip neigh show <ip>
        try {
            val process = ProcessBuilder("ip", "neigh", "show", targetIp).start()
            val reader = process.inputStream.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.contains(targetIp)) {
                    val lower = currentLine.lowercase(Locale.US)
                    if (lower.contains("lladdr") && !lower.contains("failed") && !lower.contains("incomplete")) {
                        Log.d("ScanDebug", "ARP NEIGH DIRECTO ENCONTRADO para IP=$targetIp -> $currentLine")
                        return true
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        // 2. ip neigh global
        try {
            val process = ProcessBuilder("ip", "neigh").start()
            val reader = process.inputStream.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith(targetIp) || currentLine.contains(" $targetIp ")) {
                    val lower = currentLine.lowercase(Locale.US)
                    if (lower.contains("lladdr") && !lower.contains("failed") && !lower.contains("incomplete")) {
                        Log.d("ScanDebug", "ARP NEIGH GLOBAL ENCONTRADO para IP=$targetIp -> $currentLine")
                        return true
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        // 3. Fallback mediante /proc/net/arp
        try {
            val process = ProcessBuilder("cat", "/proc/net/arp").start()
            val reader = process.inputStream.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith(targetIp) || currentLine.contains(" $targetIp ")) {
                    val parts = currentLine.trim().split("\\s+".toRegex())
                    if (parts.size >= 4) {
                        val flags = parts[2]
                        val mac = parts[3]
                        if (flags != "0x0" && mac != "00:00:00:00:00:00") {
                            Log.d("ScanDebug", "PROC NET ARP ENCONTRADO para IP=$targetIp -> MAC=$mac")
                            return true
                        }
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        return false
    }

    private fun getKernelArpTable(): Set<String> {
        val activeIps = mutableSetOf<String>()
        // Método 1: Comando shell "ip neigh"
        try {
            val process = ProcessBuilder("ip", "neigh").start()
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.trim().split("\\s+".toRegex())
                    if (parts.isNotEmpty()) {
                        val ip = parts[0]
                        if (ip.contains(".") && !line.contains("FAILED", ignoreCase = true)) {
                            activeIps.add(ip)
                        }
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        // Método 2: Fallback mediante cat /proc/net/arp
        if (activeIps.isEmpty()) {
            try {
                val process = ProcessBuilder("cat", "/proc/net/arp").start()
                process.inputStream.bufferedReader().useLines { lines ->
                    lines.drop(1).forEach { line ->
                        val parts = line.trim().split("\\s+".toRegex())
                        if (parts.size >= 4) {
                            val ip = parts[0]
                            val flags = parts[2]
                            val mac = parts[3]
                            if (flags != "0x0" && mac != "00:00:00:00:00:00") {
                                activeIps.add(ip)
                            }
                        }
                    }
                }
                process.waitFor()
            } catch (_: Exception) {}
        }
        return activeIps
    }

    private fun queryNetBiosName(ip: String): String? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 200
            val netBiosQuery = byteArrayOf(
                0x80.toByte(), 0x94.toByte(), 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x20, 0x43, 0x4B, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41,
                0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41,
                0x41, 0x00, 0x00, 0x21, 0x00, 0x01
            )
            val address = InetAddress.getByName(ip)
            val packet = DatagramPacket(netBiosQuery, netBiosQuery.size, address, 137)
            socket.send(packet)

            val recvBuf = ByteArray(1024)
            val recvPacket = DatagramPacket(recvBuf, recvBuf.size)
            socket.receive(recvPacket)
            socket.close()

            if (recvPacket.length > 57) {
                val numNames = recvBuf[56].toInt() and 0xFF
                if (numNames > 0) {
                    val nameBytes = ByteArray(15)
                    System.arraycopy(recvBuf, 57, nameBytes, 0, 15)
                    val computerName = String(nameBytes, Charsets.US_ASCII).trim()
                    if (computerName.isNotBlank()) {
                        return computerName
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun queryHttpBanner(ip: String): String? {
        val ports = intArrayOf(80, 8080, 5000, 7000, 8000, 3000)
        for (port in ports) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 200)
                val out = socket.getOutputStream()
                out.write("HEAD / HTTP/1.1\r\nHost: $ip\r\nUser-Agent: TaySecurity\r\n\r\n".toByteArray(Charsets.US_ASCII))
                out.flush()

                val buf = ByteArray(512)
                val read = socket.getInputStream().read(buf)
                socket.close()
                if (read > 0) {
                    val response = String(buf, 0, read, Charsets.US_ASCII)
                    val lower = response.lowercase(Locale.US)
                    when {
                        lower.contains("airtunes") || lower.contains("airplay") || lower.contains("darwin") || lower.contains("apple") -> return "Apple AirPlay / Mac"
                        lower.contains("chromecast") || lower.contains("google") -> return "Google Chromecast"
                        lower.contains("microsoft") || lower.contains("iis") -> return "PC Windows"
                        lower.contains("router") || lower.contains("openwrt") -> return "Router / AP"
                    }
                }
            } catch (_: Exception) {}
        }
        return null
    }

    private fun resolveAppleLocalHostnames(discoveredDevicesMap: ConcurrentHashMap<String, String>) {
        val candidates = listOf(
            "MacBook-Pro.local",
            "MacBook-Air.local",
            "MacBook.local",
            "iMac.local",
            "Mac-mini.local",
            "Mac-Studio.local",
            "iPhone.local",
            "iPad.local"
        )
        for (name in candidates) {
            try {
                val addr = InetAddress.getByName(name)
                val resolvedIp = addr.hostAddress
                if (!resolvedIp.isNullOrBlank() && resolvedIp != "127.0.0.1") {
                    val cleanName = name.removeSuffix(".local").replace("-", " ")
                    discoveredDevicesMap[resolvedIp] = cleanName
                    Log.d("ScanDebug", "RESOLUCION mDNS GENERICA -> $name -> IP=$resolvedIp")
                }
            } catch (_: Exception) {}
        }
    }

    private fun pingAddress(ip: String): Boolean {
        return try {
            val process = ProcessBuilder("ping", "-c", "1", "-w", "1", ip).start()
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (_: Exception) {
            false
        }
    }
    }

    private fun isDeviceReachable(ip: String, localIp: String): Boolean {
        return try {
            val address = InetAddress.getByName(ip)
            val localAddress = InetAddress.getByName(localIp)
            val networkInterface = NetworkInterface.getByInetAddress(localAddress)
            if (networkInterface != null && address.isReachable(networkInterface, 64, 350)) {
                true
            } else {
                address.isReachable(350)
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun generateSubnetIps(subnetPrefix: String): List<String> {
        val list = mutableListOf<String>()
        for (i in 1..254) {
            list.add("$subnetPrefix.$i")
        }
        return list
    }

    @Suppress("DEPRECATION")
    private fun startNsdDiscovery(
        context: Context,
        nsdDevices: ConcurrentHashMap<String, String>
    ): List<NsdManager.DiscoveryListener> {
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager ?: return emptyList()
        val listeners = mutableListOf<NsdManager.DiscoveryListener>()

        val serviceTypes = listOf(
            "_airplay._tcp.",
            "_companion-link._tcp.",
            "_device-info._tcp.",
            "_smb._tcp.",
            "_http._tcp.",
            "_raop._tcp."
        )

        for (serviceType in serviceTypes) {
            val listener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {
                    Log.d("ScanDebug", "NSD Discovery Iniciado para $regType")
                }
                override fun onDiscoveryStopped(regType: String) {}
                override fun onStartDiscoveryFailed(sType: String, errorCode: Int) {
                    Log.d("ScanDebug", "NSD Discovery Error en $sType: $errorCode")
                }
                override fun onStopDiscoveryFailed(sType: String, errorCode: Int) {}

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    try {
                        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(sInfo: NsdServiceInfo, eCode: Int) {}
                            override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                                val hostIp = resolvedInfo.host?.hostAddress
                                val serviceName = resolvedInfo.serviceName
                                if (!hostIp.isNullOrBlank()) {
                                    val cleanIp = hostIp.replace("%wlan0", "").replace("%1", "")
                                    nsdDevices[cleanIp] = serviceName
                                    Log.d("ScanDebug", "NSD DISPOSITIVO RESUELTO DINAMICAMENTE -> IP=$cleanIp, Nombre=$serviceName, Tipo=$serviceType")
                                }
                            }
                        })
                    } catch (_: Exception) {}
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
            }

            try {
                nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
                listeners.add(listener)
            } catch (_: Exception) {}
        }

        return listeners
    }

    private fun stopNsdDiscovery(context: Context, listeners: List<NsdManager.DiscoveryListener>) {
        if (listeners.isEmpty()) return
        try {
            val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager ?: return
            for (listener in listeners) {
                try {
                    nsdManager.stopServiceDiscovery(listener)
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun getSubnetIpRange(ipInt: Int, netmaskInt: Int): List<String> {
        if (ipInt == 0) return emptyList()

        val mask = if (netmaskInt != 0) netmaskInt else 0x00FFFFFF

        val ipBE = Integer.reverseBytes(ipInt)
        val maskBE = Integer.reverseBytes(mask)

        val networkBE = ipBE and maskBE
        val broadcastBE = networkBE or maskBE.inv()

        val ips = mutableListOf<String>()
        val start = networkBE + 1
        val end = broadcastBE - 1

        val count = (end - start + 1).coerceAtMost(512)

        for (i in 0 until count) {
            val currentBE = start + i
            val currentLE = Integer.reverseBytes(currentBE)
            val ipStr = formatIpAddress(currentLE)
            ips.add(ipStr)
        }

        return ips
    }

    @Suppress("DEPRECATION")
    private suspend fun listenMdnsResponses(
        discoveredDevicesMap: ConcurrentHashMap<String, String>,
        durationMs: Long
    ) = withContext(Dispatchers.IO) {
        var socket: MulticastSocket? = null
        val group = InetAddress.getByName("224.0.0.251")
        try {
            socket = MulticastSocket(null).apply {
                reuseAddress = true
                bind(InetSocketAddress(5353))
                timeToLive = 255
                soTimeout = 300
            }
            try {
                socket.joinGroup(group)
            } catch (_: Exception) {}

            val startTime = System.currentTimeMillis()
            val buffer = ByteArray(1472)

            while (System.currentTimeMillis() - startTime < durationMs && !socket.isClosed) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)

                    val ip = packet.address?.hostAddress ?: continue
                    val len = packet.length
                    val name = extractDeviceNameFromPacket(packet.data, len)
                    if (name != null) {
                        discoveredDevicesMap[ip] = name
                        Log.d("ScanDebug", "MULTICAST mDNS CAPTURADO -> IP=$ip, Nombre=$name")
                    }

                    // Extraer IPv4 explícitas del cuerpo del paquete A-Record de mDNS
                    for (i in 0 until len - 4) {
                        val b1 = packet.data[i].toInt() and 0xFF
                        val b2 = packet.data[i + 1].toInt() and 0xFF
                        val b3 = packet.data[i + 2].toInt() and 0xFF
                        val b4 = packet.data[i + 3].toInt() and 0xFF

                        if (b1 == 192 && b2 == 168 && b4 in 1..254) {
                            val extractedIp = "$b1.$b2.$b3.$b4"
                            val currentName = name ?: "MacBook Pro (Apple)"
                            discoveredDevicesMap[extractedIp] = currentName
                            Log.d("ScanDebug", "IPv4 extraída de mDNS -> IP=$extractedIp, Nombre=$currentName")
                        }
                    }
                } catch (_: Exception) {
                    // Timeout por ciclo
                }
            }
        } catch (e: Exception) {
            Log.d("ScanDebug", "Error en MulticastSocket mDNS: ${e.message}")
        } finally {
            try {
                socket?.leaveGroup(group)
            } catch (_: Exception) {}
            try {
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    private fun extractDeviceNameFromPacket(data: ByteArray, length: Int): String? {
        try {
            val strBuilder = StringBuilder()
            val namesFound = mutableListOf<String>()

            val ignoredKeywords = setOf(
                "local", "arpa", "in-addr", "dns-sd", "udp", "tcp", "services",
                "device-info", "airplay", "http", "https", "comunion-link", "_tcp", "_udp"
            )

            for (i in 0 until length) {
                val b = data[i].toInt() and 0xFF
                val c = b.toChar()
                if ((c in 'a'..'z') || (c in 'A'..'Z') || (c in '0'..'9') || c == '-' || c == '_') {
                    strBuilder.append(c)
                } else {
                    if (strBuilder.length >= 4) {
                        val candidate = strBuilder.toString()
                        val lower = candidate.lowercase(Locale.US)
                        if (lower.contains("macbook") || lower.contains("imac") || lower.contains("mac") ||
                            lower.contains("iphone") || lower.contains("ipad") || lower.contains("apple") ||
                            lower.contains("desktop") || lower.contains("laptop") || lower.contains("win") ||
                            lower.contains("galaxy") || lower.contains("android") || lower.contains("tv")) {
                            return candidate
                        }
                        if (candidate.length >= 5 && !ignoredKeywords.contains(lower) && !candidate.startsWith("_")) {
                            namesFound.add(candidate)
                        }
                    }
                    strBuilder.setLength(0)
                }
            }
            if (strBuilder.length >= 4) {
                val candidate = strBuilder.toString()
                val lower = candidate.lowercase(Locale.US)
                if (lower.contains("macbook") || lower.contains("imac") || lower.contains("mac") ||
                    lower.contains("iphone") || lower.contains("ipad") || lower.contains("apple") ||
                    lower.contains("desktop") || lower.contains("laptop") || lower.contains("win") ||
                    lower.contains("galaxy") || lower.contains("android") || lower.contains("tv")) {
                    return candidate
                }
                if (candidate.length >= 5 && !ignoredKeywords.contains(lower) && !candidate.startsWith("_")) {
                    namesFound.add(candidate)
                }
            }
            return namesFound.firstOrNull()
        } catch (_: Exception) {}
        return null
    }

    private fun sendSubnetBroadcast(subnetPrefix: String) {
        try {
            val socket = DatagramSocket()
            socket.broadcast = true
            val mdnsData = createAppleMdnsQueryPayload()

            val broadcastAddresses = listOf(
                InetAddress.getByName("$subnetPrefix.255"),
                InetAddress.getByName("255.255.255.255"),
                InetAddress.getByName("224.0.0.251")
            )

            val ports = intArrayOf(5353, 137, 1900)

            for (addr in broadcastAddresses) {
                for (port in ports) {
                    try {
                        val packet = DatagramPacket(mdnsData, mdnsData.size, addr, port)
                        socket.send(packet)
                    } catch (_: Exception) {}
                }
            }
            socket.close()
        } catch (_: Exception) {}
    }

    private fun sendUdpProbes(ip: String) {
        try {
            val socket = DatagramSocket()
            socket.soTimeout = 100
            val appleMdnsPayload = createAppleMdnsQueryPayload()
            val dummyData = byteArrayOf(0x00, 0x00, 0x00, 0x00)
            val address = InetAddress.getByName(ip)

            val ports = intArrayOf(5353, 137, 1900, 5355, 62078)

            for (port in ports) {
                try {
                    val data = if (port == 5353) appleMdnsPayload else dummyData
                    val packet = DatagramPacket(data, data.size, address, port)
                    socket.send(packet)
                } catch (_: Exception) {}
            }
            socket.close()
        } catch (_: Exception) {}
    }

    private fun queryDeviceMdnsUnicast(ip: String, timeoutMs: Int = 300): String? {
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.soTimeout = timeoutMs
            val address = InetAddress.getByName(ip)

            val mdnsPayload = createAppleMdnsQueryPayload()
            val packet = DatagramPacket(mdnsPayload, mdnsPayload.size, address, 5353)
            socket.send(packet)

            val reversePayload = createReverseMdnsQueryPayload(ip)
            if (reversePayload != null) {
                val reversePacket = DatagramPacket(reversePayload, reversePayload.size, address, 5353)
                socket.send(reversePacket)
            }

            val recvBuf = ByteArray(1472)
            val recvPacket = DatagramPacket(recvBuf, recvBuf.size)
            socket.receive(recvPacket)

            val len = recvPacket.length
            if (len > 12) {
                val foundName = extractDeviceNameFromPacket(recvPacket.data, len)
                Log.d("ScanDebug", "mDNS Unicast EXITOSO para IP=$ip -> len=$len, nombre=$foundName")
                return foundName ?: "Dispositivo Apple / Mac"
            }
            return null
        } catch (_: Exception) {
            return null
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    private fun createAppleMdnsQueryPayload(): ByteArray {
        val header = byteArrayOf(
            0x00, 0x00,
            0x00, 0x00,
            0x00, 0x03,
            0x00, 0x00,
            0x00, 0x00,
            0x00, 0x00
        )

        val q1 = byteArrayOf(
            0x08, '_'.code.toByte(), 'a'.code.toByte(), 'i'.code.toByte(), 'r'.code.toByte(), 'p'.code.toByte(), 'l'.code.toByte(), 'a'.code.toByte(), 'y'.code.toByte(),
            0x04, '_'.code.toByte(), 't'.code.toByte(), 'c'.code.toByte(), 'p'.code.toByte(),
            0x05, 'l'.code.toByte(), 'o'.code.toByte(), 'c'.code.toByte(), 'a'.code.toByte(), 'l'.code.toByte(),
            0x00, 0x00, 0x0C, 0x80.toByte(), 0x01
        )

        val q2 = byteArrayOf(
            0x0F, '_'.code.toByte(), 'c'.code.toByte(), 'o'.code.toByte(), 'm'.code.toByte(), 'a'.code.toByte(), 'n'.code.toByte(), 'i'.code.toByte(), 'o'.code.toByte(), 'n'.code.toByte(), '-'.code.toByte(), 'l'.code.toByte(), 'i'.code.toByte(), 'n'.code.toByte(), 'f'.code.toByte(),
            0x04, '_'.code.toByte(), 't'.code.toByte(), 'c'.code.toByte(), 'p'.code.toByte(),
            0x05, 'l'.code.toByte(), 'o'.code.toByte(), 'c'.code.toByte(), 'a'.code.toByte(), 'l'.code.toByte(),
            0x00, 0x00, 0x0C, 0x80.toByte(), 0x01
        )

        val q3 = byteArrayOf(
            0x0C, '_'.code.toByte(), 'd'.code.toByte(), 'e'.code.toByte(), 'v'.code.toByte(), 'i'.code.toByte(), 'c'.code.toByte(), 'e'.code.toByte(), '-'.code.toByte(), 'i'.code.toByte(), 'n'.code.toByte(), 'f'.code.toByte(), 'o'.code.toByte(),
            0x04, '_'.code.toByte(), 't'.code.toByte(), 'c'.code.toByte(), 'p'.code.toByte(),
            0x05, 'l'.code.toByte(), 'o'.code.toByte(), 'c'.code.toByte(), 'a'.code.toByte(), 'l'.code.toByte(),
            0x00, 0x00, 0x0C, 0x80.toByte(), 0x01
        )

        return header + q1 + q2 + q3
    }

    private fun createReverseMdnsQueryPayload(ip: String): ByteArray? {
        val parts = ip.split(".")
        if (parts.size != 4) return null
        val revIp = "${parts[3]}.${parts[2]}.${parts[1]}.${parts[0]}.in-addr.arpa"
        val header = byteArrayOf(
            0x00, 0x00,
            0x00, 0x00,
            0x00, 0x01,
            0x00, 0x00,
            0x00, 0x00,
            0x00, 0x00
        )
        val baos = java.io.ByteArrayOutputStream()
        baos.write(header)
        for (part in revIp.split(".")) {
            val bytes = part.toByteArray(Charsets.US_ASCII)
            baos.write(bytes.size)
            baos.write(bytes)
        }
        baos.write(0)
        baos.write(byteArrayOf(0x00, 0x0C, 0x80.toByte(), 0x01))
        return baos.toByteArray()
    }

    private fun tryPort(ip: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), timeoutMs)
            socket.close()
            true
        } catch (_: java.net.ConnectException) {
            // Un TCP RST (ConnectException) indica un host activo solo si el kernel de Android confirmo su MAC en la tabla de vecinos ARP
            var isArpValid = false
            try {
                val process = ProcessBuilder("ip", "neigh").start()
                val reader = process.inputStream.bufferedReader()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: continue
                    if (currentLine.startsWith(ip) || currentLine.contains(" $ip ")) {
                        val lower = currentLine.lowercase(Locale.US)
                        if (lower.contains("lladdr") && !lower.contains("failed") && !lower.contains("incomplete")) {
                            isArpValid = true
                            break
                        }
                    }
                }
                process.waitFor()
            } catch (_: Exception) {}
            isArpValid
        } catch (_: Exception) {
            false
        }
    }

    private fun getHostName(ip: String): String? {
        return try {
            val inetAddress = InetAddress.getByName(ip)
            val canonical = inetAddress.canonicalHostName
            if (canonical != ip && canonical.isNotBlank()) canonical else inetAddress.hostName.takeIf { it != ip }
        } catch (_: Exception) {
            null
        }
    }

    private fun determineDeviceType(ip: String, gatewayIp: String, hostName: String): String {
        val lower = hostName.lowercase(Locale.US)
        return when {
            ip == gatewayIp -> "Router / Gateway"
            lower.contains("macbook") || lower.contains("imac") || lower.contains("mac-mini") || lower.contains("apple") || lower.contains("darwin") || lower.contains("mac") -> "Ordenador (Mac)"
            lower.contains("iphone") || lower.contains("ipad") || lower.contains("ios") -> "Móvil (Apple iOS)"
            lower.contains("android") || lower.contains("galaxy") || lower.contains("pixel") || lower.contains("xiaomi") || lower.contains("redmi") || lower.contains("oppo") || lower.contains("realme") || lower.contains("huawei") -> "Móvil (Android)"
            lower.contains("tv") || lower.contains("chromecast") || lower.contains("roku") || lower.contains("firestick") -> "Smart TV / Media"
            lower.contains("printer") || lower.contains("epson") || lower.contains("hp") || lower.contains("canon") -> "Impresora"
            lower.contains("desktop") || lower.contains("laptop") || lower.contains("pc") || lower.contains("win") -> "Ordenador / Laptop"
            else -> "Dispositivo de red"
        }
    }

    private fun formatIpAddress(ip: Int): String {
        return String.format(
            Locale.US,
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }

    private fun ipToLong(ip: String): Long {
        val parts = ip.split(".")
        if (parts.size != 4) return 0L
        return try {
            (parts[0].toLong() shl 24) + (parts[1].toLong() shl 16) + (parts[2].toLong() shl 8) + parts[3].toLong()
        } catch (_: Exception) {
            0L
        }
    }
