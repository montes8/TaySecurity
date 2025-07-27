package com.tay.taysecurity.android.ui.home.info

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.ui.home.extra.AndroidLogo


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InfoScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopBarBack {
                navController.popBackStack()
            }
        }
    ){
        Column(modifier = Modifier.background(Color.Black)
            .fillMaxSize().padding(16.dp),
            horizontalAlignment =Alignment.CenterHorizontally ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                AndroidLogo(
                    backgroundColor = Color.Black,
                    contentColor =  Color.White,
                    padding = 30.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "BLOQUEO DE LLAMADAS",
                color = Color.Magenta,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_bold))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Para utilizar esta funcionalidad, lo primero que se debe hacer el poner esta aplicacion como la aplicacion predeterminada para llamadas" +
                        ", y activar los permisos necesarios que le apareceran cuando ingrese a detalle de esta aplicacion, desde la opcion configuracion," +
                        " aplicacionesy buscar esta aplicacion, luego ingresar a permisos y activarlos",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_bold))
            )
        }
    }


}