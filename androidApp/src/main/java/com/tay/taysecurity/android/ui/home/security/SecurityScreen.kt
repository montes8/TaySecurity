package com.tay.taysecurity.android.ui.home.security

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.DialogSure
import com.tay.taysecurity.android.component.TayCardItemNext
import com.tay.taysecurity.android.component.TayCardItemSwitch
import com.tay.taysecurity.android.component.navigation.DestinationsMain
import com.tay.taysecurity.android.ui.home.blocking.BlockingViewModel
import com.tay.taysecurity.android.ui.home.service.LocationServiceTay
import com.tay.taysecurity.android.utils.modeDeveloperAndMockLocation

@Composable
fun SecurityScreen(navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel : BlockingViewModel = hiltViewModel()
    var modeDevDialog by remember { mutableStateOf(false) }

    DialogSure(  modeDevDialog,true, dismissDialog = {
        modeDevDialog = false
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 140.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de\nseguridad",
            color = Color.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "Optener informacion de Image",
            subText ="Esta opción te permitira acceder a la metadata de una imagen y ver toda suu información."){
            navController.navigate(DestinationsMain.ImageScreen)
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
        TayCardItemSwitch(state =viewModel.uiState.securty.simulationGps,
            text = "Crea una ubicación aleatoria",
            subText ="Esta opción creara una ubicación aleatoria cada sierto tiempo, " +
                    "debes habilitar como app de localización en la opcion de desarrollador para esta opción.") {
                if (it){
                    if(context.modeDeveloperAndMockLocation()){
                        try {
                        viewModel.updateDataSecurity(true,4)
                        loadService(context)
                        }catch (e: Exception){
                            e.printStackTrace()
                            modeDevDialog = true
                        }

                    }else{
                        modeDevDialog = true
                    }
                }else{
                    viewModel.updateDataSecurity(false,4)
                    disableService(context)
                }
          }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Si no funciona, asegurese de haber definido esta app como ubicación de prueba como indica en la sección de ayuda.",
            fontSize = 14.sp,
            color = Color.Red,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}


fun loadService(context: Context){
    try {
        disableService(context)
        context.startService(Intent(context, LocationServiceTay::class.java))
    }catch (e: Exception){
        e.printStackTrace()
    }
}

fun disableService(context: Context){
    try {
        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        lm.removeTestProvider(LocationManager.GPS_PROVIDER)
        context.stopService(Intent(context, LocationServiceTay::class.java))
    }catch (e: Exception){
        e.printStackTrace()
    }
}