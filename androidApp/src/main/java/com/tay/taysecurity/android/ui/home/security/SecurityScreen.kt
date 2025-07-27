package com.tay.taysecurity.android.ui.home.security

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItemNext
import com.tay.taysecurity.android.component.TayCardItemSwitch
import com.tay.taysecurity.android.component.navigation.DestinationsMain
import com.tay.taysecurity.android.utils.tayToast

@Composable
fun SecurityScreen(navController: NavHostController
) {
    val checkedGps = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top= 12.dp, start = 8.dp, end = 8.dp, bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de\nseguridad",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "Optener informacion de Image",
            subText ="Esta opción te permitira acceder a la metadata de una imagen y ver toda suu información."){
            navController.navigate(DestinationsMain.ImageScreen.route)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Para usar esta funcionalidad debes habilitar modo desarrolador obten mas infomacion en la seccion de ayuda para saber como usarlo",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemSwitch(state =checkedGps.value,
            text = "Crea una ubicación aleatoria",
            subText ="Esta opción creara una ubicación aleatoria cada sierto tiempo, " +
                    "debes habilitar como app de localización en la opcion de desarrollador para esta opción."){
            checkedGps.value = it
            context.tayToast("Funcionalidad aun no disponible")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}