package com.tay.taysecurity.android.ui.home.blocking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BlockingScreen(
) {

    val checkedCall = remember { mutableStateOf(false) }
    val checkedCallTotal = remember { mutableStateOf(false) }
    val checkedMesage = remember { mutableStateOf(false) }
    val checkedMesageTotal = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de contactos",
            style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Black,elevation = 4.dp) {
            Row{
                Column(modifier = Modifier
                    .weight(4.0f)
                    .padding(16.dp)) {
                    Text(
                        text = "Bloqueo de llamadas",
                        style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                    Text(
                        text = "Esta opción bloqueara las llamadas entrantes no registradas en tus contactos",
                        style = TextStyle(color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedCall.value,
                    onCheckedChange = {
                        checkedCall.value = it
                    },colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Magenta,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor =  Color.Magenta,
                        uncheckedTrackColor = Color.White,
                    )

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Black,elevation = 4.dp) {
            Row{
                Column(modifier = Modifier
                    .weight(4.0f)
                    .padding(16.dp)) {
                    Text(
                        text = "Bloqueo de llamadas total",
                        style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                    Text(
                        text = "Esta opción bloqueara total las llamadas entrantes en su telfono",
                        style = TextStyle(color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedCallTotal.value,
                    onCheckedChange = {
                        checkedCallTotal.value = it
                    },colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Magenta,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor =  Color.Magenta,
                        uncheckedTrackColor = Color.White,
                    )

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Black,elevation = 4.dp) {
            Row{
                Column(modifier = Modifier
                    .weight(4.0f)
                    .padding(16.dp)) {
                    Text(
                        text = "Bloqueo de mensajes",
                        style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                    Text(
                        text = "Esta opción bloqueara las mensaje entrantes no registradas en tus contactos",
                        style = TextStyle(color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedMesage.value,
                    onCheckedChange = {
                        checkedMesage.value = it
                    },colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Magenta,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor =  Color.Magenta,
                        uncheckedTrackColor = Color.White,
                    )

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Black,elevation = 4.dp) {
            Row{
                Column(modifier = Modifier
                    .weight(4.0f)
                    .padding(16.dp)) {
                    Text(
                        text = "Bloqueo de mensajes total",
                        style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                    Text(
                        text = "Esta opción bloqueara todos los mensaje entrantes en su telefono",
                        style = TextStyle(color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Default),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedMesageTotal.value,
                    onCheckedChange = {
                        checkedMesageTotal.value = it
                    },colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Magenta,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor =  Color.Magenta,
                        uncheckedTrackColor = Color.White,
                    )

                )
            }
        }

    }
}