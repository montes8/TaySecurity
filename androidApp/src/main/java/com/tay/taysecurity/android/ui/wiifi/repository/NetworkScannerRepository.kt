package com.tay.taysecurity.android.ui.wiifi.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.tay.taysecurity.android.ui.wiifi.model.AvailableWifiNetwork
import com.tay.taysecurity.android.ui.wiifi.model.CellularNetworkInfo
import com.tay.taysecurity.android.ui.wiifi.model.ConnectedNetworkInfo
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkScannerRepository {
    fun hasLocationPermission(): Boolean
    fun getConnectedNetworkInfo(): ConnectedNetworkInfo?
    fun getAvailableWifiNetworks(): List<AvailableWifiNetwork>
    fun getCellularInfo(): CellularNetworkInfo?
}

@Singleton
class NetworkScannerRepositoryImpl @Inject constructor(
    private val context: Application
) : NetworkScannerRepository {

    override fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun getConnectedNetworkInfo(): ConnectedNetworkInfo? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return null

        val activeNetwork = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return null

        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        if (!isWifi && !isCellular) return null

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        if (isWifi) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val wifiInfo: WifiInfo? = wifiManager?.connectionInfo
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)

            val rawSsid = wifiInfo?.ssid?.replace("\"", "") ?: "Red Wi-Fi Conectada"
            val ssid = if (rawSsid == "<unknown ssid>" || rawSsid.isBlank()) "Wi-Fi Activo" else rawSsid
            val ipAddress = formatIpAddress(wifiInfo?.ipAddress ?: 0)
            val linkSpeed = wifiInfo?.linkSpeed ?: 0
            val frequency = formatFrequency(wifiInfo?.frequency ?: 0)
            val bssid = wifiInfo?.bssid ?: "00:00:00:00:00:00"
            val dnsServers = linkProperties?.dnsServers?.joinToString(", ") { it.hostAddress ?: "" }?.takeIf { it.isNotBlank() } ?: "No disponible"
            val maxRx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) wifiInfo?.maxSupportedRxLinkSpeedMbps ?: 0 else 0
            val maxTx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) wifiInfo?.maxSupportedTxLinkSpeedMbps ?: 0 else 0

            return ConnectedNetworkInfo(
                ssid = ssid,
                ipAddress = ipAddress,
                isWifi = true,
                hasInternet = hasInternet,
                linkSpeedMbps = linkSpeed,
                frequencyGhz = frequency,
                securityType = "WPA2/WPA3",
                gateway = getGatewayIp(wifiManager),
                bssid = bssid,
                maxRxSpeedMbps = maxRx,
                maxTxSpeedMbps = maxTx,
                dnsServers = dnsServers,
                subnetMask = "255.255.255.0"
            )
        } else {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val operator = telephonyManager?.networkOperatorName?.takeIf { it.isNotBlank() } ?: "Red Móvil"

            return ConnectedNetworkInfo(
                ssid = "Datos Móviles ($operator)",
                ipAddress = "Asignada por Operador",
                isWifi = false,
                hasInternet = hasInternet,
                linkSpeedMbps = 0,
                frequencyGhz = getNetworkTypeString(telephonyManager),
                securityType = "Red Celular Encriptada",
                gateway = "Operador Móvil",
                bssid = "N/A",
                maxRxSpeedMbps = 0,
                maxTxSpeedMbps = 0,
                dnsServers = "No disponible",
                subnetMask = "N/A"
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun getAvailableWifiNetworks(): List<AvailableWifiNetwork> {
        if (!hasLocationPermission()) return emptyList()

        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                ?: return emptyList()

        return try {
            val scanResults = wifiManager.scanResults ?: emptyList()

            val dualBandBaseSsids = scanResults.groupBy { getBaseSsid(it.SSID?.replace("\"", "") ?: "") }
                .filter { (_, results) ->
                    val has24 = results.any { it.frequency in 2400..2500 }
                    val has5 = results.any { it.frequency in 4900..5900 }
                    has24 && has5
                }.keys

            scanResults.mapNotNull { result ->
                val ssid = result.SSID?.replace("\"", "") ?: ""
                if (ssid.isBlank()) return@mapNotNull null

                val level = WifiManager.calculateSignalLevel(result.level, 100)
                val capabilities = result.capabilities ?: ""
                val isSecured = !capabilities.contains("OPEN") && !capabilities.contains("ESS")
                val frequencyGhz = formatFrequency(result.frequency)
                val isHotspot = isMobileHotspot(ssid)
                val hasDual = dualBandBaseSsids.contains(getBaseSsid(ssid))

                val wifiStandard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    when (result.wifiStandard) {
                        ScanResult.WIFI_STANDARD_LEGACY -> "Wi-Fi Legacy (802.11a/b/g)"
                        ScanResult.WIFI_STANDARD_11N -> "Wi-Fi 4 (802.11n)"
                        ScanResult.WIFI_STANDARD_11AC -> "Wi-Fi 5 (802.11ac)"
                        ScanResult.WIFI_STANDARD_11AX -> "Wi-Fi 6 / 6E (802.11ax)"
                        ScanResult.WIFI_STANDARD_11AD -> "Wi-Fi 6 (6GHz)"
                        ScanResult.WIFI_STANDARD_11BE -> "Wi-Fi 7 (802.11be)"
                        else -> "Estándar Desconocido"
                    }
                } else {
                    "Wi-Fi Estándar"
                }

                val freqMHz = result.frequency
                val rssi = result.level
                val exp = (27.55 - (20 * kotlin.math.log10(freqMHz.toDouble())) + kotlin.math.abs(rssi.toDouble())) / 20.0
                val estMeters = Math.pow(10.0, exp)
                val distanceText = String.format(java.util.Locale.getDefault(), "~%.1f metros", estMeters)

                val freq = result.frequency
                val channel = when {
                    freq == 2484 -> 14
                    freq in 2412..2472 -> (freq - 2412) / 5 + 1
                    freq in 5170..5825 -> (freq - 5000) / 5
                    else -> 0
                }

                val channelWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (result.channelWidth) {
                        ScanResult.CHANNEL_WIDTH_20MHZ -> "20 MHz"
                        ScanResult.CHANNEL_WIDTH_40MHZ -> "40 MHz"
                        ScanResult.CHANNEL_WIDTH_80MHZ -> "80 MHz"
                        ScanResult.CHANNEL_WIDTH_160MHZ -> "160 MHz"
                        else -> "20/40 MHz"
                    }
                } else {
                    "20 MHz"
                }

                val isPasspoint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.isPasspointNetwork else false
                val venueName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.venueName?.toString()?.takeIf { it.isNotBlank() } ?: "No especificado" else "No especificado"
                val centerFreqs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) "0: ${result.centerFreq0} MHz, 1: ${result.centerFreq1} MHz" else "N/A"
                val beaconAgeMs = if (result.timestamp > 0) {
                    val ageSec = (android.os.SystemClock.elapsedRealtimeNanos() / 1000 - result.timestamp) / 1000000.0
                    String.format(java.util.Locale.getDefault(), "%.1fs", ageSec)
                } else {
                    "N/A"
                }

                AvailableWifiNetwork(
                    ssid = ssid,
                    bssid = result.BSSID ?: "00:00:00:00:00:00",
                    signalPercentage = level,
                    rssidBm = result.level,
                    isSecured = isSecured,
                    securityType = parseSecurityType(capabilities),
                    frequencyGhz = frequencyGhz,
                    isMobileHotspot = isHotspot,
                    wifiStandard = wifiStandard,
                    distanceText = distanceText,
                    channel = channel,
                    channelWidth = channelWidth,
                    capabilities = capabilities,
                    hasDualBand = hasDual,
                    isPasspoint = isPasspoint,
                    venueName = venueName,
                    centerFreqs = centerFreqs,
                    beaconAgeMs = beaconAgeMs
                )
            }.distinctBy { it.bssid }.sortedByDescending { it.signalPercentage }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    override fun getCellularInfo(): CellularNetworkInfo? {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                ?: return null

        val operatorName = telephonyManager.networkOperatorName.takeIf { it.isNotBlank() } ?: "Sin Cobertura"
        val type = getNetworkTypeString(telephonyManager)

        return CellularNetworkInfo(
            operatorName = operatorName,
            networkType = type,
            signalPercentage = 85
        )
    }

    private fun isMobileHotspot(ssid: String): Boolean {
        val lowerSsid = ssid.lowercase()
        val keywords = listOf("iphone", "galaxy", "redmi", "pixel", "xiaomi", "moto", "hotspot", "huawei", "oppo", "vivo", "realme")
        return keywords.any { lowerSsid.contains(it) }
    }

    private fun parseSecurityType(capabilities: String): String {
        return when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            capabilities.contains("OPEN") -> "Red Abierta"
            else -> "Segura"
        }
    }

    private fun formatIpAddress(ip: Int): String {
        return try {
            val bytes = byteArrayOf(
                (ip and 0xff).toByte(),
                (ip shr 8 and 0xff).toByte(),
                (ip shr 16 and 0xff).toByte(),
                (ip shr 24 and 0xff).toByte()
            )
            InetAddress.getByAddress(bytes).hostAddress ?: "0.0.0.0"
        } catch (_: Exception) {
            "0.0.0.0"
        }
    }

    private fun getGatewayIp(wifiManager: WifiManager?): String {
        return try {
            val dhcp = wifiManager?.dhcpInfo ?: return "192.168.1.1"
            formatIpAddress(dhcp.gateway)
        } catch (_: Exception) {
            "192.168.1.1"
        }
    }

    private fun formatFrequency(freq: Int): String {
        return when {
            freq in 2400..2499 -> "2.4 GHz"
            freq in 4900..5899 -> "5.0 GHz"
            freq in 5925..7125 -> "6.0 GHz (Wi-Fi 6E)"
            else -> "$freq MHz"
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNetworkTypeString(telephonyManager: TelephonyManager?): String {
        if (telephonyManager == null) return "Desconocido"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (telephonyManager.dataNetworkType) {
                    TelephonyManager.NETWORK_TYPE_NR -> "5G"
                    TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
                    TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSPA -> "3G+"
                    TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
                    else -> "Móvil Red"
                }
            } else {
                "4G LTE"
            }
        } catch (_: Exception) {
            "4G/5G"
        }
    }

    private fun getBaseSsid(ssid: String): String {
        return ssid.lowercase()
            .replace(Regex("(_5g|_5ghz| 5g| 5ghz|_5GHz|_5G)$"), "")
            .replace(Regex("(_2g|_2ghz| 2g| 2ghz|_2GHz|_2G)$"), "")
            .trim()
    }
}
