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
            val netBiosInfo = queryNetBiosInfo(ip)
            val ssdpInfo = querySsdpInfo(ip)
            val httpTitleOrBanner = queryHttpBannerAndTitle(ip)
            val unicastMdnsName = queryDeviceMdnsUnicast(ip, 300)
            val canonicalDnsName = getHostName(ip)

            val netBiosName = netBiosInfo?.name
            val netBiosMac = netBiosInfo?.mac

            val arpMacAddress = getMacAddressForIp(ip)
            val macAddress = netBiosMac ?: arpMacAddress

            val vendorName = lookupVendorByMac(macAddress)

            val rawHostName = discoveredHostName 
                ?: netBiosName 
                ?: canonicalDnsName 
                ?: unicastMdnsName 
                ?: httpTitleOrBanner
                ?: ssdpInfo?.serverOrModel

            val defaultName = when {
                ip == gatewayIp -> "Router Wi-Fi"
                vendorName != null -> vendorName
                netBiosName != null -> "PC Windows ($netBiosName)"
                else -> "Dispositivo de red"
            }

            val initialHostName = if (rawHostName != null && rawHostName != ip) rawHostName else defaultName
            val (inferredName, inferredType) = inferDeviceTypeAndName(ip, gatewayIp, initialHostName, macAddress)

            return@withContext DeviceScanResult(
                ip = ip,
                hostName = inferredName,
                deviceType = inferredType,
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

    private fun getMacAddressForIp(targetIp: String): String? {
        try {
            val process = ProcessBuilder("ip", "neigh").start()
            val reader = process.inputStream.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith(targetIp) || currentLine.contains(" $targetIp ")) {
                    val lower = currentLine.lowercase(Locale.US)
                    if (lower.contains("lladdr")) {
                        val parts = lower.split("\\s+".toRegex())
                        val lladdrIndex = parts.indexOf("lladdr")
                        if (lladdrIndex != -1 && lladdrIndex + 1 < parts.size) {
                            val mac = parts[lladdrIndex + 1].trim()
                            if (mac.contains(":") && mac != "00:00:00:00:00:00") {
                                return mac
                            }
                        }
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        try {
            val process = ProcessBuilder("cat", "/proc/net/arp").start()
            val reader = process.inputStream.bufferedReader()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.startsWith(targetIp) || currentLine.contains(" $targetIp ")) {
                    val parts = currentLine.trim().split("\\s+".toRegex())
                    if (parts.size >= 4) {
                        val mac = parts[3].trim()
                        if (mac.contains(":") && mac != "00:00:00:00:00:00") {
                            return mac
                        }
                    }
                }
            }
            process.waitFor()
        } catch (_: Exception) {}

        return null
    }

    private fun lookupVendorByMac(mac: String?): String? {
        if (mac == null || mac.length < 8) return null
        val cleanMac = mac.lowercase(Locale.US).replace("-", ":")
        val prefix = cleanMac.take(8)

        return when {
            // Xiaomi / Redmi / POCO
            prefix.startsWith("00:26:08") || prefix.startsWith("18:65:90") || prefix.startsWith("28:6a:ba") ||
                    prefix.startsWith("38:f9:d3") || prefix.startsWith("64:09:80") || prefix.startsWith("70:10:5c") ||
                    prefix.startsWith("8c:85:90") || prefix.startsWith("a4:50:46") || prefix.startsWith("bc:d2:95") ||
                    prefix.startsWith("f8:a4:5f") || prefix.startsWith("04:cf:8c") || prefix.startsWith("0c:98:38") ||
                    prefix.startsWith("14:ab:c5") || prefix.startsWith("20:47:da") || prefix.startsWith("34:80:b3") ||
                    prefix.startsWith("3c:bd:3e") || prefix.startsWith("44:23:7c") || prefix.startsWith("50:ec:d8") ||
                    prefix.startsWith("68:20:94") || prefix.startsWith("74:a7:ea") || prefix.startsWith("7c:1d:d9") ||
                    prefix.startsWith("84:28:fd") || prefix.startsWith("88:c3:97") || prefix.startsWith("9c:20:7b") ||
                    prefix.startsWith("a0:86:c6") || prefix.startsWith("b0:e2:35") || prefix.startsWith("c4:3c:b0") ||
                    prefix.startsWith("d4:f5:13") || prefix.startsWith("e4:46:da") || prefix.startsWith("ec:d0:9f") -> "Xiaomi (Android)"

            // Motorola / Lenovo
            prefix.startsWith("00:0c:e7") || prefix.startsWith("00:14:a4") || prefix.startsWith("00:1a:66") ||
                    prefix.startsWith("00:22:a3") || prefix.startsWith("14:30:04") || prefix.startsWith("30:96:fb") ||
                    prefix.startsWith("40:88:05") || prefix.startsWith("70:56:81") || prefix.startsWith("7c:d9:ac") ||
                    prefix.startsWith("84:a1:34") || prefix.startsWith("a4:08:ea") || prefix.startsWith("bc:f5:ac") ||
                    prefix.startsWith("e0:1a:e2") || prefix.startsWith("f8:e0:79") || prefix.startsWith("00:11:09") ||
                    prefix.startsWith("00:1e:45") || prefix.startsWith("18:82:8c") || prefix.startsWith("44:e1:37") ||
                    prefix.startsWith("60:be:b5") || prefix.startsWith("88:71:b1") || prefix.startsWith("c8:1f:be") ||
                    prefix.startsWith("d0:37:42") || prefix.startsWith("e8:91:20") || prefix.startsWith("f4:f5:db") -> "Motorola (Android)"

            // Oppo / Realme / Vivo / OnePlus
            prefix.startsWith("04:3c:d1") || prefix.startsWith("10:2a:b3") || prefix.startsWith("1c:77:f6") ||
                    prefix.startsWith("28:a0:3b") || prefix.startsWith("3c:cd:93") || prefix.startsWith("4c:11:bf") ||
                    prefix.startsWith("50:2b:73") || prefix.startsWith("64:a2:f9") || prefix.startsWith("70:8a:09") ||
                    prefix.startsWith("8c:0b:92") || prefix.startsWith("98:10:94") || prefix.startsWith("a4:c7:de") ||
                    prefix.startsWith("b4:6b:fc") || prefix.startsWith("c8:32:00") || prefix.startsWith("d4:89:50") ||
                    prefix.startsWith("e8:b4:c8") || prefix.startsWith("f4:09:d8") -> "Oppo / Realme / Vivo (Android)"

            // Huawei / Honor
            prefix.startsWith("00:1e:10") || prefix.startsWith("00:25:9e") || prefix.startsWith("00:46:4b") ||
                    prefix.startsWith("00:66:4b") || prefix.startsWith("00:e0:fc") || prefix.startsWith("04:02:1f") ||
                    prefix.startsWith("08:19:a6") || prefix.startsWith("0c:37:dc") || prefix.startsWith("10:1b:54") ||
                    prefix.startsWith("10:44:00") || prefix.startsWith("10:c6:1f") || prefix.startsWith("14:3d:f2") ||
                    prefix.startsWith("14:a0:f8") || prefix.startsWith("18:c5:8a") || prefix.startsWith("1c:15:1f") ||
                    prefix.startsWith("20:0b:c7") || prefix.startsWith("20:ab:48") || prefix.startsWith("24:09:95") ||
                    prefix.startsWith("24:69:68") || prefix.startsWith("24:7f:20") || prefix.startsWith("24:df:6a") ||
                    prefix.startsWith("28:31:52") || prefix.startsWith("28:5f:db") || prefix.startsWith("28:6e:d4") ||
                    prefix.startsWith("2c:97:b1") || prefix.startsWith("30:74:96") || prefix.startsWith("30:d1:7e") ||
                    prefix.startsWith("34:2e:b4") || prefix.startsWith("34:cd:6d") || prefix.startsWith("38:37:8b") -> "Huawei / Honor (Android)"

            // Microsoft / Windows PC
            prefix.startsWith("00:03:ff") || prefix.startsWith("00:0d:3a") || prefix.startsWith("00:12:5a") ||
                    prefix.startsWith("00:15:5d") || prefix.startsWith("00:17:fa") || prefix.startsWith("00:1d:d8") ||
                    prefix.startsWith("00:22:48") || prefix.startsWith("00:25:ae") || prefix.startsWith("00:50:f2") ||
                    prefix.startsWith("28:18:78") || prefix.startsWith("30:59:b7") || prefix.startsWith("48:50:73") ||
                    prefix.startsWith("50:1a:c5") || prefix.startsWith("58:fb:84") || prefix.startsWith("60:45:bd") ||
                    prefix.startsWith("60:be:b5") || prefix.startsWith("70:b5:e8") || prefix.startsWith("7c:1e:52") ||
                    prefix.startsWith("98:5f:d3") || prefix.startsWith("a0:ce:c8") || prefix.startsWith("b8:27:eb") ||
                    prefix.startsWith("b8:31:b5") || prefix.startsWith("ba:00:2a") || prefix.startsWith("c0:25:e9") ||
                    prefix.startsWith("dc:b4:c4") || prefix.startsWith("00:13:e8") || prefix.startsWith("00:14:22") ||
                    prefix.startsWith("00:19:b9") || prefix.startsWith("00:1c:23") || prefix.startsWith("00:21:70") -> "PC Windows / Microsoft"

            // Intel / Realtek / Dell / HP / Lenovo (Tarjetas Wi-Fi comunes de PC Windows)
            prefix.startsWith("00:1b:21") || prefix.startsWith("00:1c:c0") || prefix.startsWith("00:1e:67") ||
                    prefix.startsWith("00:21:6a") || prefix.startsWith("00:22:fb") || prefix.startsWith("00:23:14") ||
                    prefix.startsWith("00:24:d7") || prefix.startsWith("00:27:0e") || prefix.startsWith("00:aa:00") ||
                    prefix.startsWith("08:11:96") || prefix.startsWith("0c:8b:fd") || prefix.startsWith("10:02:b5") ||
                    prefix.startsWith("10:08:b1") || prefix.startsWith("14:da:e9") || prefix.startsWith("18:03:73") ||
                    prefix.startsWith("1c:69:7a") || prefix.startsWith("20:7d:74") || prefix.startsWith("24:4b:fe") ||
                    prefix.startsWith("28:d2:44") || prefix.startsWith("2c:60:0c") || prefix.startsWith("30:52:cb") ||
                    prefix.startsWith("34:13:e8") || prefix.startsWith("38:b1:db") || prefix.startsWith("3c:a8:2a") ||
                    prefix.startsWith("40:16:9f") || prefix.startsWith("44:85:00") || prefix.startsWith("48:51:b7") ||
                    prefix.startsWith("4c:1d:96") || prefix.startsWith("50:7b:9d") || prefix.startsWith("54:ee:75") ||
                    prefix.startsWith("58:91:cf") || prefix.startsWith("5c:51:4f") || prefix.startsWith("60:1d:91") ||
                    prefix.startsWith("64:00:6a") || prefix.startsWith("68:05:ca") || prefix.startsWith("6c:3b:e5") ||
                    prefix.startsWith("70:1c:e7") || prefix.startsWith("74:70:fd") || prefix.startsWith("78:0c:b8") ||
                    prefix.startsWith("7c:b0:c2") || prefix.startsWith("80:00:0b") || prefix.startsWith("84:3a:4b") ||
                    prefix.startsWith("88:ae:dd") || prefix.startsWith("8c:dc:d4") || prefix.startsWith("90:2e:1c") ||
                    prefix.startsWith("94:e6:f7") || prefix.startsWith("98:40:bb") || prefix.startsWith("9c:b6:d0") ||
                    prefix.startsWith("a0:36:9f") || prefix.startsWith("a4:4e:31") || prefix.startsWith("a8:1e:84") ||
                    prefix.startsWith("ac:72:89") || prefix.startsWith("b0:25:aa") || prefix.startsWith("b4:2e:99") ||
                    prefix.startsWith("b8:81:98") || prefix.startsWith("bc:a8:a6") || prefix.startsWith("c0:3c:59") ||
                    prefix.startsWith("c4:b3:01") || prefix.startsWith("c8:5b:76") || prefix.startsWith("cc:96:e5") ||
                    prefix.startsWith("d0:57:7b") || prefix.startsWith("d8:9c:67") || prefix.startsWith("dc:71:96") ||
                    prefix.startsWith("e0:d5:5e") || prefix.startsWith("e4:a7:a0") || prefix.startsWith("e8:b1:fc") ||
                    prefix.startsWith("ec:88:8f") || prefix.startsWith("f0:1d:27") || prefix.startsWith("f4:06:16") ||
                    prefix.startsWith("f8:32:e4") || prefix.startsWith("fc:34:97") -> "PC Windows / Microsoft"

            else -> null
        }
    }

    data class NetBiosInfo(val name: String, val mac: String?)
    data class SsdpInfo(val serverOrModel: String)

    private fun queryNetBiosInfo(ip: String): NetBiosInfo? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 250
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

                    val unitIdOffset = 57 + (numNames * 18)
                    var macStr: String? = null
                    if (recvPacket.length >= unitIdOffset + 6) {
                        macStr = String.format(
                            Locale.US,
                            "%02x:%02x:%02x:%02x:%02x:%02x",
                            recvBuf[unitIdOffset].toInt() and 0xFF,
                            recvBuf[unitIdOffset + 1].toInt() and 0xFF,
                            recvBuf[unitIdOffset + 2].toInt() and 0xFF,
                            recvBuf[unitIdOffset + 3].toInt() and 0xFF,
                            recvBuf[unitIdOffset + 4].toInt() and 0xFF,
                            recvBuf[unitIdOffset + 5].toInt() and 0xFF
                        )
                    }
                    if (computerName.isNotBlank()) {
                        return NetBiosInfo(computerName, macStr)
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun querySsdpInfo(ip: String): SsdpInfo? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 250
            val ssdpQuery = ("M-SEARCH * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "MAN: \"ssdp:discover\"\r\n" +
                    "MX: 1\r\n" +
                    "ST: ssdp:all\r\n\r\n").toByteArray(Charsets.US_ASCII)

            val address = InetAddress.getByName(ip)
            val packet = DatagramPacket(ssdpQuery, ssdpQuery.size, address, 1900)
            socket.send(packet)

            val buf = ByteArray(1024)
            val recvPacket = DatagramPacket(buf, buf.size)
            socket.receive(recvPacket)
            socket.close()

            val response = String(buf, 0, recvPacket.length, Charsets.US_ASCII)
            var serverOrModel: String? = null
            for (line in response.split("\r\n")) {
                val lineLower = line.lowercase(Locale.US)
                if (lineLower.startsWith("server:") || lineLower.startsWith("usn:") || lineLower.startsWith("location:")) {
                    serverOrModel = line.substringAfter(":").trim()
                }
            }
            if (!serverOrModel.isNullOrBlank()) SsdpInfo(serverOrModel) else null
        } catch (_: Exception) {
            null
        }
    }

    private fun queryHttpBannerAndTitle(ip: String): String? {
        val ports = intArrayOf(80, 8080, 5000, 7000, 8000, 3000)
        for (port in ports) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 200)
                val out = socket.getOutputStream()
                out.write("GET / HTTP/1.1\r\nHost: $ip\r\nUser-Agent: Mozilla/5.0\r\nConnection: close\r\n\r\n".toByteArray(Charsets.US_ASCII))
                out.flush()

                val buf = ByteArray(1024)
                val read = socket.getInputStream().read(buf)
                socket.close()
                if (read > 0) {
                    val response = String(buf, 0, read, Charsets.US_ASCII)
                    val lower = response.lowercase(Locale.US)
                    if (lower.contains("<title>")) {
                        val title = response.substringAfter("<title>", "").substringBefore("</title>", "").trim()
                        if (title.isNotBlank() && title.length < 50) {
                            return title
                        }
                    }
                    for (line in response.split("\r\n")) {
                        if (line.lowercase(Locale.US).startsWith("server:")) {
                            val server = line.substringAfter(":").trim()
                            if (server.isNotBlank()) return server
                        }
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

    private fun inferDeviceTypeAndName(ip: String, gatewayIp: String, currentName: String, macAddress: String?): Pair<String, String> {
        if (ip == gatewayIp || ip.endsWith(".1")) {
            val name = if (currentName == "Dispositivo de red") "Router Wi-Fi" else currentName
            return Pair(name, "Router / Gateway")
        }

        val lower = currentName.lowercase(Locale.US)

        // 1. Windows PC / Laptop (Evaluado primero para evitar falsos positivos con Chrome)
        if (lower.contains("desktop") || lower.contains("laptop") || lower.contains("pc") || lower.contains("win") || lower.contains("workstation") || lower.contains("microsoft") || lower.contains("dell") || lower.contains("hp") || lower.contains("lenovo") || lower.contains("thinkpad") || lower.contains("surface") || lower.contains("acer") || lower.contains("asus") || lower.contains("msi")) {
            return Pair(currentName, "Ordenador (Windows)")
        }

        if (queryWsDiscovery(ip) != null || tryPort(ip, 5357, 100) || tryPort(ip, 445, 100) || tryPort(ip, 139, 100) || tryPort(ip, 135, 100) || tryPort(ip, 3389, 100)) {
            val name = if (currentName == "Dispositivo de red") "PC / Laptop Windows" else currentName
            return Pair(name, "Ordenador (Windows)")
        }

        // 2. Apple Mac / iOS
        if (lower.contains("macbook") || lower.contains("imac") || lower.contains("mac") || lower.contains("apple") || lower.contains("darwin")) {
            return Pair(currentName, "Ordenador (Mac)")
        }
        if (lower.contains("iphone") || lower.contains("ipad") || (lower.contains("ios") && !lower.contains("netbios"))) {
            return Pair(currentName, "Móvil (Apple iOS)")
        }

        if (tryPort(ip, 5000, 100) || tryPort(ip, 7000, 100) || tryPort(ip, 5900, 100) || tryPort(ip, 631, 100)) {
            val name = if (currentName == "Dispositivo de red") "MacBook / Dispositivo Apple" else currentName
            return Pair(name, "Ordenador (Mac)")
        }

        // 3. Android
        if (lower.contains("android") || lower.contains("galaxy") || lower.contains("samsung") || lower.contains("pixel") ||
            lower.contains("xiaomi") || lower.contains("redmi") || lower.contains("poco") || lower.contains("oppo") ||
            lower.contains("realme") || lower.contains("vivo") || lower.contains("oneplus") || lower.contains("huawei") ||
            lower.contains("honor") || lower.contains("motorola") || lower.contains("moto") || lower.contains("nokia")) {
            return Pair(currentName, "Móvil (Android)")
        }

        val castName = queryGoogleCastDeviceInfo(ip)
        if (castName != null || tryPort(ip, 5555, 100)) {
            val name = if (currentName == "Dispositivo de red" && castName != null) castName else if (currentName == "Dispositivo de red") "Móvil Android" else currentName
            return Pair(name, "Móvil (Android)")
        }

        // 4. Por defecto para clientes en red Wi-Fi
        val defaultName = if (currentName == "Dispositivo de red") "Móvil / Smartphone" else currentName
        return Pair(defaultName, "Móvil (Android / iOS)")
    }

    private fun isRandomizedMac(mac: String): Boolean {
        val clean = mac.replace("-", ":").lowercase(Locale.US)
        if (clean.length >= 2) {
            val secondChar = clean[1]
            return secondChar == '2' || secondChar == '6' || secondChar == 'a' || secondChar == 'e'
        }
        return false
    }

    private fun queryWsDiscovery(ip: String): String? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 250
            val uuid = java.util.UUID.randomUUID().toString()
            val probe = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:wsd=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\">" +
                    "<soap:Header><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To>" +
                    "<wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action>" +
                    "<wsa:MessageID>urn:uuid:$uuid</wsa:MessageID></soap:Header>" +
                    "<soap:Body><wsd:Probe/></soap:Body></soap:Envelope>").toByteArray(Charsets.UTF_8)

            val address = InetAddress.getByName(ip)
            val packet = DatagramPacket(probe, probe.size, address, 3702)
            socket.send(packet)

            val buf = ByteArray(2048)
            val recvPacket = DatagramPacket(buf, buf.size)
            socket.receive(recvPacket)
            socket.close()

            val xml = String(buf, 0, recvPacket.length, Charsets.UTF_8)
            val lower = xml.lowercase(Locale.US)
            if (lower.contains("microsoft") || lower.contains("windows") || lower.contains("wsd") || lower.contains("pub")) {
                "PC Windows"
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun queryGoogleCastDeviceInfo(ip: String): String? {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, 8008), 200)
            val out = socket.getOutputStream()
            out.write("GET /setup/eureka/device_info HTTP/1.1\r\nHost: $ip:8008\r\nUser-Agent: Mozilla/5.0\r\n\r\n".toByteArray(Charsets.US_ASCII))
            out.flush()

            val buf = ByteArray(1024)
            val read = socket.getInputStream().read(buf)
            socket.close()
            if (read > 0) {
                val json = String(buf, 0, read, Charsets.UTF_8)
                val lower = json.lowercase(Locale.US)
                if (lower.contains("eureka") || lower.contains("chromecast") || lower.contains("model_name") || lower.contains("build_version")) {
                    val name = json.substringAfter("\"name\":\"", "").substringBefore("\"", "").trim()
                    if (name.isNotBlank() && name.length < 40) {
                        return name
                    }
                    return "Móvil Android"
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun determineDeviceType(ip: String, gatewayIp: String, hostName: String): String {
        val lower = hostName.lowercase(Locale.US)
        return when {
            ip == gatewayIp -> "Router / Gateway"

            // Apple Mac
            lower.contains("macbook") || lower.contains("imac") || lower.contains("mac-mini") ||
                    lower.contains("mac-studio") || lower.contains("mac-pro") || lower.contains("apple") ||
                    lower.contains("darwin") || lower.contains("mac") -> "Ordenador (Mac)"

            // Apple iOS
            lower.contains("iphone") || lower.contains("ipad") || lower.contains("ipod") ||
                    lower.contains("watch") || lower.contains("ios") -> "Móvil (Apple iOS)"

            // Android (Todas las marcas populares)
            lower.contains("android") || lower.contains("galaxy") || lower.contains("samsung") ||
                    lower.contains("pixel") || lower.contains("xiaomi") || lower.contains("redmi") ||
                    lower.contains("poco") || lower.contains("oppo") || lower.contains("realme") ||
                    lower.contains("vivo") || lower.contains("oneplus") || lower.contains("huawei") ||
                    lower.contains("honor") || lower.contains("motorola") || lower.contains("moto") ||
                    lower.contains("nokia") || lower.contains("zte") || lower.contains("tecno") ||
                    lower.contains("infinix") || lower.contains("xperia") || lower.contains("lenovo") ||
                    lower.contains("asus") || lower.contains("tcl") -> "Móvil (Android)"

            // Windows PC (Todas las marcas y palabras clave)
            lower.contains("desktop") || lower.contains("laptop") || lower.contains("pc") ||
                    lower.contains("win") || lower.contains("windows") || lower.contains("workstation") ||
                    lower.contains("surface") || lower.contains("thinkpad") || lower.contains("msi") ||
                    lower.contains("dell") || lower.contains("hp") || lower.contains("acer") -> "Ordenador (Windows)"

            // Smart TV / Multimedia
            lower.contains("tv") || lower.contains("chromecast") || lower.contains("roku") ||
                    lower.contains("firestick") || lower.contains("firetv") || lower.contains("bravia") ||
                    lower.contains("webos") || lower.contains("tizen") || lower.contains("mibox") ||
                    lower.contains("mi-box") || lower.contains("apple-tv") -> "Smart TV / Media"

            // Impresoras
            lower.contains("printer") || lower.contains("epson") || lower.contains("hp") ||
                    lower.contains("canon") || lower.contains("brother") || lower.contains("xerox") ||
                    lower.contains("lexmark") || lower.contains("ricoh") -> "Impresora"

            else -> {
                // Inspección dinámica por puerto/servicio cuando el nombre no es descriptivo
                if (tryPort(ip, 5357, 100) || tryPort(ip, 445, 100) || tryPort(ip, 139, 100) || tryPort(ip, 135, 100)) {
                    "Ordenador (Windows)"
                } else if (tryPort(ip, 5555, 100) || tryPort(ip, 8008, 100)) {
                    "Móvil (Android)"
                } else if (tryPort(ip, 5000, 100) || tryPort(ip, 7000, 100) || tryPort(ip, 5900, 100)) {
                    "Dispositivo Apple / Mac"
                } else {
                    "Dispositivo de red"
                }
            }
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
}
