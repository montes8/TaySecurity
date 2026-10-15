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
    val bssid: String
)

data class AvailableWifiNetwork(
    val ssid: String,
    val bssid: String,
    val signalPercentage: Int,
    val rssidBm: Int,
    val isSecured: Boolean,
    val securityType: String,
    val frequencyGhz: String,
    val isMobileHotspot: Boolean
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
