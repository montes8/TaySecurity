package com.tay.taysecurity.android.ui.home.blocking

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.R

@Composable
fun BlockingScreen(
) {

    val checkedCall = remember { mutableStateOf(false) }
    val checkedCallTotal = remember { mutableStateOf(false) }
    val checkedMessage = remember { mutableStateOf(false) }
    val checkedMessageTotal = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de \nbloqueos",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_bold))
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
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.padding( top = 2.dp),
                        text = "Esta opción bloqueara las llamadas entrantes no registradas en tus contactos",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
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
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.padding( top = 2.dp),
                        text = "Esta opción bloqueara total las llamadas entrantes en su telfono",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
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
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.padding( top = 2.dp),
                        text = "Esta opción bloqueara las mensaje entrantes no registradas en tus contactos",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedMessage.value,
                    onCheckedChange = {
                        checkedMessage.value = it
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
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.padding( top = 2.dp),
                        text = "Esta opción bloqueara todos los mensaje entrantes en su telefono",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = checkedMessageTotal.value,
                    onCheckedChange = {
                        checkedMessageTotal.value = it
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