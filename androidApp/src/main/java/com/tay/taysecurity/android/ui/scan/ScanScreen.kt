package com.tay.taysecurity.android.ui.scan

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.utils.uiTayBackAction

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    onOpenRouterAdmin: (String) -> Unit = {}
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.startScan()
    }

    Scaffold(
        topBar = {
            TopBarBack(title = "Escáner Red Wi-Fi", openClick = uiTayBackAction())
        }
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Wifi Info Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFF181818),
                border = BorderStroke(1.dp, Color.Magenta),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = if (state.isWifiConnected) Color.Magenta else Color.Red,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (state.isWifiConnected) state.wifiName else "Desconectado",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.gabi_regular))
                            )
                        }

                        if (state.isWifiConnected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Magenta.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${state.devices.size} Dispositivos",
                                    color = Color.Magenta,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                                )
                            }
                        }
                    }

                    if (state.isWifiConnected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "IP Móvil:",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                                )
                                Text(
                                    text = state.localIp,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "IP Router:",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                                )
                                Text(
                                    text = state.gatewayIp,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                                )
                            }
                        }
                    } else if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.gabi_regular))
                        )
                    }

                    if (state.isWifiConnected && state.gatewayIp.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onOpenRouterAdmin(state.gatewayIp) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Router,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Abrir Configuración del Router",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.gabi_regular))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scan Action Button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Color.Magenta),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    disabledBackgroundColor = Color.DarkGray
                ),
                enabled = !state.isScanning && state.isWifiConnected,
                onClick = { viewModel.startScan() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isScanning) {
                        CircularProgressIndicator(
                            color = Color.Magenta,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escaneando...",
                            color = Color.Magenta,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.gabi_regular))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escanear Red Wi-Fi",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.gabi_regular))
                        )
                    }
                }
            }

            if (state.isScanning) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = state.progress,
                    color = Color.Magenta,
                    backgroundColor = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Procesando direcciones: ${state.scannedCount} / ${state.totalCount}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Devices List
            if (state.devices.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.devices,
                        key = { it.ip }
                    ) { device ->
                        DeviceItemCard(device = device)
                    }
                }
            } else if (!state.isScanning && state.isWifiConnected) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Presiona 'Escanear Red Wi-Fi' para buscar todos los dispositivos.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.gabi_regular))
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceItemCard(device: DeviceScanResult) {
    val (icon, iconColor, badgeText) = when {
        device.isGateway -> Triple(Icons.Default.Router, Color.Magenta, "Router")
        device.isSelf -> Triple(Icons.Default.Smartphone, Color.Green, "Tu Móvil")
        device.deviceType.contains("Windows", ignoreCase = true) || device.deviceType.contains("Laptop", ignoreCase = true) || device.deviceType.contains("Ordenador (Windows)", ignoreCase = true) || device.hostName.contains("PC", ignoreCase = true) || device.hostName.contains("Windows", ignoreCase = true) || device.hostName.contains("Desktop", ignoreCase = true) || device.hostName.contains("Laptop", ignoreCase = true) -> Triple(Icons.Default.Laptop, Color(0xFF00ADEF), "Laptop / PC")
        device.deviceType.contains("Mac", ignoreCase = true) || device.hostName.contains("MacBook", ignoreCase = true) || device.hostName.contains("iMac", ignoreCase = true) -> Triple(Icons.Default.Computer, Color(0xFFA2AAAD), "Apple Mac")
        (device.deviceType.contains("Apple iOS", ignoreCase = true) || device.deviceType.contains("iPhone", ignoreCase = true) || device.deviceType.contains("iPad", ignoreCase = true) || (device.deviceType.contains("iOS", ignoreCase = true) && !device.deviceType.contains("NetBIOS", ignoreCase = true) && !device.hostName.contains("NetBIOS", ignoreCase = true))) -> Triple(Icons.Default.PhoneIphone, Color(0xFFA2AAAD), "Apple iOS")
        device.deviceType.contains("Android", ignoreCase = true) -> Triple(Icons.Default.Android, Color(0xFF3DDC84), "Android")
        device.deviceType.contains("Móvil", ignoreCase = true) || device.deviceType.contains("Smartphone", ignoreCase = true) -> Triple(Icons.Default.Smartphone, Color(0xFF00BCD4), "Móvil")
        device.deviceType.contains("TV", ignoreCase = true) || device.deviceType.contains("Media", ignoreCase = true) -> Triple(Icons.Default.Tv, Color(0xFFFF9800), "Smart TV")
        device.deviceType.contains("Impresora", ignoreCase = true) -> Triple(Icons.Default.Print, Color(0xFF9C27B0), "Impresora")
        else -> Triple(Icons.Default.Lan, Color.Cyan, "Red Local")
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF141414),
        border = BorderStroke(
            1.dp,
            if (device.isGateway) Color.Magenta else if (device.isSelf) Color.Green else Color(0xFF333333)
        ),
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.ip,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                )
                Text(
                    text = device.hostName,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.2f))
                    .border(1.dp, iconColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    color = iconColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.gabi_regular))
                )
            }
        }
    }
}
