package com.tay.taysecurity.android.ui.wiifi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tay.taysecurity.android.ui.BaseViewModel
import com.tay.taysecurity.android.ui.wiifi.model.NetworkUiState
import com.tay.taysecurity.android.ui.wiifi.repository.NetworkScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val repository: NetworkScannerRepository
) : BaseViewModel() {

    var uiState by mutableStateOf(NetworkUiState())
        private set

    init {
        startNetworkScan()
    }

    fun startNetworkScan() {
        execute {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            delay(300)

            val hasPermission = repository.hasLocationPermission()
            val connected = repository.getConnectedNetworkInfo()
            val available = repository.getAvailableWifiNetworks()
            val cellular = repository.getCellularInfo()

            uiState = uiState.copy(
                isLoading = false,
                hasLocationPermission = hasPermission,
                connectedNetwork = connected,
                availableWifiNetworks = available,
                cellularInfo = cellular
            )
        }
    }
}
