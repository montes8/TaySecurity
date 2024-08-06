package com.tay.taysecurity.android.ui.home.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItem

@Composable
fun SecurityScreen(
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top= 8.dp, start = 8.dp, end = 8.dp, bottom = 78.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de \nbloqueos",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_bold))
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =true,
            text = "Bloqueo de capturas y grabacion de pantalla",
            subText ="Esta opción no permite tomar capturas ni grabar la pantalla en este dispositivo."){

        }
        Spacer(modifier = Modifier.height(16.dp))
        TayCardItem(state =true,
            text = "Bloqueo de grabacion de audio",
            subText ="Esta opción no permite grabar audios."){

        }
    }
}