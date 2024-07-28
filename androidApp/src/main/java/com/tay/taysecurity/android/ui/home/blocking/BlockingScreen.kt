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
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItem
import com.tay.taysecurity.android.ui.blocking.BlockingViewModel

@Composable
fun BlockingScreen(
) {
    val viewModel : BlockingViewModel = hiltViewModel()
  //  val checkedCall = remember { mutableStateOf(false) }
   // val checkedCallTotal = remember { mutableStateOf(false) }
  //  val checkedMessage = remember { mutableStateOf(false) }
   // val checkedMessageTotal = remember { mutableStateOf(false) }

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
        TayCardItem(state =viewModel.uiState.securty.blockingCallFull,
            text = "Bloqueo de Todas  las llamadas.",
            subText = "Esta opción bloqueara las llamadas entrantes a tu telefono movil."){
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =viewModel.uiState.securty.blockingCall,
            text = "Bloqueo de llamadas desconocidas",
            subText = "Esta opción bloqueara total las llamadas entrantes que no esten registrados en tus contactos."){
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =viewModel.uiState.securty.blockingSmsFull,
            text = "Bloqueo de todos los mensajes entrantes",
            subText = "Esta opción bloqueara y eliminara todos los mensaje entrantes en tu bandeja de entrada."){
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Black,elevation = 4.dp) {
            Row{
                Column(modifier = Modifier
                    .weight(4.0f)
                    .padding(16.dp)) {
                    Text(
                        text = "Bloqueo de mensajes desconocidos",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.padding( top = 2.dp),
                        text = "Esta opción bloqueara y borrara los mensajes desconocidos entrantes en tu bandeja de entrada.",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
                        color = Color.White
                    )
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    checked = viewModel.uiState.securty.blockingSms,
                    onCheckedChange = {
                        viewModel.uiState.securty.blockingSms = it
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