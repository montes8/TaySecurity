package com.tay.taysecurity.android.ui.home.blocking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItem

@Composable
fun BlockingScreen(
) {
    val viewModel : BlockingViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top= 8.dp, start = 8.dp, end = 8.dp, bottom = 24.dp),
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
            viewModel.updateDataSecurity(it,0)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =viewModel.uiState.securty.blockingCall,
            text = "Bloqueo de llamadas desconocidas",
            subText = "Esta opción bloqueara total las llamadas entrantes que no esten registrados en tus contactos."){
            viewModel.updateDataSecurity(it,1)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =viewModel.uiState.securty.blockingSmsFull,
            text = "Bloqueo de todos los mensajes entrantes",
            subText = "Esta opción bloqueara y eliminara todos los mensaje entrantes en tu bandeja de entrada."){
            viewModel.updateDataSecurity(it,2)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =viewModel.uiState.securty.blockingSms,
            text = "Bloqueo de mensajes desconocidos",
            subText ="Esta opción bloqueara y borrara los mensajes desconocidos entrantes en tu bandeja de entrada."){
            viewModel.updateDataSecurity(it,3)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =true,
            text = "Bloqueo de capturas y grabacion de pantalla",
            subText ="Esta opción no permite tomar capturas ni grabar la pantalla en este dispositivo."){
            viewModel.updateDataSecurity(it,3)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =true,
            text = "Bloqueo de grabacion de audio",
            subText ="Esta opción no permite grabar audios."){
            viewModel.updateDataSecurity(it,3)
        }
    }
}