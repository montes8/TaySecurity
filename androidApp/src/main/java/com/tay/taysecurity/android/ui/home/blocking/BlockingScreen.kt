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
import com.tay.taysecurity.model.SecurityShared

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
            viewModel.updateDataSecurity(it)
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
        TayCardItem(state =viewModel.uiState.securty.blockingSms,
            text = "Bloqueo de mensajes desconocidos",
            subText ="Esta opción bloqueara y borrara los mensajes desconocidos entrantes en tu bandeja de entrada."){
        }
    }
}