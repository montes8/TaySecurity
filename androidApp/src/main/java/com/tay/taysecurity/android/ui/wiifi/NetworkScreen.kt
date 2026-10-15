package com.tay.taysecurity.android.ui.wiifi

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.ui.wiifi.model.AvailableWifiNetwork
import com.tay.taysecurity.android.ui.wiifi.model.ConnectedNetworkInfo
import com.tay.taysecurity.android.utils.uiTayBackAction

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopBarBack(
                title = "Escáner de Redes",
                openClick = uiTayBackAction()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diagnóstico de Conexión",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { viewModel.startNetworkScan() },
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.Magenta,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Escanear",
                            tint = Color.Magenta
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    ConnectedNetworkCard(
                        connectedNetwork = state.connectedNetwork
                    )
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = null,
                            tint = Color.Magenta,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Redes Wi-Fi y Zonas Móviles Cercanas (${state.availableWifiNetworks.size})",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!state.hasLocationPermission) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = Color(0xFF221515),
                            border = BorderStroke(1.dp, Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Permiso de Ubicación Necesario",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Android requiere permisos de ubicación para listar las redes Wi-Fi y Hotspots móviles cercanos.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (state.hasLocationPermission && state.availableWifiNetworks.isEmpty() && !state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Presiona escanear para buscar redes Wi-Fi al alcance.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 3. Lista de Redes Disponibles
                items(state.availableWifiNetworks) { wifi ->
                    AvailableWifiItem(wifi = wifi)
                }
            }
        }
    }
}

@Composable
fun ConnectedNetworkCard(
    connectedNetwork: ConnectedNetworkInfo?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF181818),
        border = BorderStroke(1.dp, if (connectedNetwork?.hasInternet == true) Color.Green else Color.Magenta),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (connectedNetwork?.isWifi == true) Icons.Default.Wifi else Icons.Default.SignalCellularAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = connectedNetwork?.ssid ?: "Buscando Red...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Estado de Internet
                if (connectedNetwork != null) {
                    val badgeColor = if (connectedNetwork.hasInternet) Color(0xFF2E7D32) else Color(0xFFC62828)
                    val statusText = if (connectedNetwork.hasInternet) "Con Internet" else "Sin Internet"
                    val icon = if (connectedNetwork.hasInternet) Icons.Default.CheckCircle else Icons.Default.Error

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = badgeColor
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = statusText,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (connectedNetwork != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "IP Local", color = Color.Gray, fontSize = 11.sp)
                        Text(text = connectedNetwork.ipAddress, color = Color.LightGray, fontSize = 13.sp)
                    }

                    Column {
                        Text(text = "Frecuencia", color = Color.Gray, fontSize = 11.sp)
                        Text(text = connectedNetwork.frequencyGhz, color = Color.LightGray, fontSize = 13.sp)
                    }

                    if (connectedNetwork.isWifi) {
                        Column {
                            Text(text = "Velocidad", color = Color.Gray, fontSize = 11.sp)
                            Text(text = "${connectedNetwork.linkSpeedMbps} Mbps", color = Color.LightGray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableWifiItem(wifi: AvailableWifiNetwork) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF1A1A1A),
        border = BorderStroke(
            1.dp,
            if (wifi.isMobileHotspot) Color(0xFFFF9800) else Color(0xFF333333)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (wifi.isMobileHotspot) Icons.Default.PhonelinkRing else Icons.Default.Wifi,
                        contentDescription = null,
                        tint = if (wifi.isMobileHotspot) Color(0xFFFF9800) else Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = wifi.ssid,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (wifi.isMobileHotspot) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Card(
                                    shape = RoundedCornerShape(4.dp),
                                    backgroundColor = Color(0xFFE65100)
                                ) {
                                    Text(
                                        text = "Móvil",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${wifi.frequencyGhz} • ${wifi.securityType}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (wifi.isSecured) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = if (wifi.isSecured) Color.Gray else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${wifi.signalPercentage}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = wifi.signalPercentage / 100f,
                color = when {
                    wifi.signalPercentage > 70 -> Color.Green
                    wifi.signalPercentage > 40 -> Color.Yellow
                    else -> Color.Red
                },
                backgroundColor = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
    }
}
