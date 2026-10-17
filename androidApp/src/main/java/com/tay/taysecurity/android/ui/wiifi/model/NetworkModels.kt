package com.tay.taysecurity.android.ui.wiifi.model

data class ConnectedNetworkInfo(
    val ssid: String,
    val ipAddress: String,
    val isWifi: Boolean,
    val hasInternet: Boolean,
    val linkSpeedMbps: Int,
    val frequencyGhz: String,
    val securityType: String,
    val gateway: String,
    val bssid: String,
    val maxRxSpeedMbps: Int,
    val maxTxSpeedMbps: Int,
    val dnsServers: String,
    val subnetMask: String
)

data class AvailableWifiNetwork(
    val ssid: String,
    val bssid: String,
    val signalPercentage: Int,
    val rssidBm: Int,
    val isSecured: Boolean,
    val securityType: String,
    val frequencyGhz: String,
    val isMobileHotspot: Boolean,
    val wifiStandard: String,
    val distanceText: String,
    val channel: Int,
    val channelWidth: String,
    val capabilities: String,
    val hasDualBand: Boolean,
    val isPasspoint: Boolean,
    val venueName: String,
    val centerFreqs: String,
    val beaconAgeMs: String
)

data class CellularNetworkInfo(
    val operatorName: String,
    val networkType: String,
    val signalPercentage: Int
)

data class NetworkUiState(
    val isLoading: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val connectedNetwork: ConnectedNetworkInfo? = null,
    val availableWifiNetworks: List<AvailableWifiNetwork> = emptyList(),
    val cellularInfo: CellularNetworkInfo? = null,
    val errorMessage: String? = null
)
