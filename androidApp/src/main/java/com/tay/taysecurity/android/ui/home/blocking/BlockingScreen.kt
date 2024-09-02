package com.tay.taysecurity.android.ui.home.blocking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItemSwitch

@Composable
fun BlockingScreen(
) {

    val checkedAudio = remember { mutableStateOf(false) }
    val viewModel : BlockingViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top= 8.dp, start = 8.dp, end = 8.dp, bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de \nbloqueos",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_bold))
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingCallFull,
            text = stringResource(R.string.title_all_call),
            subText = stringResource(R.string.sub_title_all_call)){
            viewModel.updateDataSecurity(it,0)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingCall,
            text = stringResource(R.string.title_stranger_call),
            subText = stringResource(R.string.sub_title_stranger_call)){
            viewModel.updateDataSecurity(it,1)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSmsFull,
            text = "Bloqueo de todos los mensajes entrantes",
            subText = "Esta opción bloqueara y eliminara todos los mensaje entrantes en tu bandeja de entrada."){
            viewModel.updateDataSecurity(it,2)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =viewModel.uiState.securty.blockingSms,
            text = "Bloqueo de mensajes desconocidos",
            subText ="Esta opción bloqueara y borrara los mensajes desconocidos entrantes en tu bandeja de entrada."){
            viewModel.updateDataSecurity(it,3)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =checkedAudio.value,
            text = "Bloqueo de grabacion de audio",
            subText ="Esta opción no permite grabar audios."){
            checkedAudio.value = it
        }
    }
}