package com.tay.taysecurity.android.ui.wiifi

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.ui.wiifi.model.AvailableWifiNetwork

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NetworkDetailScreen(
    network: AvailableWifiNetwork,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarBack(
                title = network.ssid,
                openClick = onBack
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderDetailCard(network)
            }

            item {
                Text(
                    text = "Detalles Técnicos Avanzados",
                    color = Color.Magenta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                DetailItemCard(
                    title = "Distancia Estimada",
                    techValue = network.distanceText,
                    techExplanation = "Cálculo físico aproximado basado en la atenuación de la señal de radio (RSSI) y la frecuencia."
                )
            }

            item {
                DetailItemCard(
                    title = "Estándar Wi-Fi",
                    techValue = network.wifiStandard,
                    techExplanation = "Protocolo de comunicación IEEE 802.11 utilizado por el punto de acceso (ej. ax, ac, n)."
                )
            }

            item {
                DetailItemCard(
                    title = "Frecuencia y Canal",
                    techValue = "${network.frequencyGhz} (Canal ${network.channel})",
                    techExplanation = "Frecuencia de portadora RF en GHz y número de canal de radiofrecuencia asignado."
                )
            }

            item {
                DetailItemCard(
                    title = "Ancho de Canal (Channel Width)",
                    techValue = network.channelWidth,
                    techExplanation = "Ancho de banda del canal asignado en MHz (20MHz, 40MHz, 80MHz, 160MHz)."
                )
            }

            item {
                DetailItemCard(
                    title = "Frecuencias Centrales",
                    techValue = network.centerFreqs,
                    techExplanation = "Frecuencias centrales de los segmentos de canal para anchos de banda grandes (80MHz/160MHz)."
                )
            }

            item {
                DetailItemCard(
                    title = "Intensidad de Señal",
                    techValue = "${network.signalPercentage}% (${network.rssidBm} dBm)",
                    techExplanation = "Indicador de Potencia de Señal Recibida (RSSI) medido en dBm y normalizado a porcentaje."
                )
            }

            item {
                DetailItemCard(
                    title = "Antigüedad de Baliza (Beacon Age)",
                    techValue = network.beaconAgeMs,
                    techExplanation = "Tiempo transcurrido desde que el router emitió el último paquete de baliza capturado."
                )
            }

            item {
                DetailItemCard(
                    title = "Seguridad y Encriptación",
                    techValue = network.securityType,
                    techExplanation = "Protocolo de autenticación y cifrado de tramas inalámbricas (WPA2, WPA3, OWE, Abierta)."
                )
            }

            item {
                DetailItemCard(
                    title = "Passpoint (Hotspot 2.0)",
                    techValue = if (network.isPasspoint) "Soportado" else "No Soportado",
                    techExplanation = "Indica si la red soporta autenticación automática empresarial o pública Passpoint / HS2.0."
                )
            }

            item {
                DetailItemCard(
                    title = "Proveedor / Venue Name",
                    techValue = network.venueName,
                    techExplanation = "Nombre comercial del operador o entidad que opera el punto de acceso (Hotspot 2.0)."
                )
            }

            item {
                DetailItemCard(
                    title = "BSSID (Dirección MAC del Router)",
                    techValue = network.bssid,
                    techExplanation = "Identificador único de hardware (Media Access Control) de la interfaz de radio del punto de acceso."
                )
            }
        }
    }
}

@Composable
fun HeaderDetailCard(network: AvailableWifiNetwork) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF181818),
        border = BorderStroke(1.dp, if (network.isMobileHotspot) Color(0xFFFF9800) else Color.Magenta),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = null,
                        tint = Color.Magenta,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = network.ssid,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (network.isMobileHotspot) "Zona Móvil / Hotspot" else "Red Wi-Fi Fija",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItemCard(
    title: String,
    techValue: String,
    techExplanation: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF1A1A1A),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = techValue,
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFB0BEC5),
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Detalle Técnico:",
                        color = Color(0xFFB0BEC5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = techExplanation,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
