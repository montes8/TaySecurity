package com.tay.taysecurity.android.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.ScreenIdentifier
import com.tay.taysecurity.android.utils.manager.TaySureCall

@Composable
fun ScreenHome(navController: NavController){

    Column(modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier
            .fillMaxWidth()
            .height(56.dp).padding(top = 24.dp)
            .background(colorResource(R.color.black))){
            Text(text = stringResource(R.string.app_title), modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically), color = colorResource(R.color.white)
                , textAlign = TextAlign.Center,  style = typography.bodyMedium,
                fontSize = 20.sp, )
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp),verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(modifier = Modifier.fillMaxWidth()
                .height(48.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.black)),
                onClick = {
                    TaySureCall.taySureFinalizeCall()
                  //  navController.navigate(ScreenIdentifier.BlockingCallScreen.route)
                }) {
                Text(text = "Configuración de llamada", color = Color.White,
                    style = typography.bodyMedium,
                    fontSize = 16.sp)
            }
            Divider(color = Color.Black, thickness = 24.dp)
            Button(modifier = Modifier.fillMaxWidth()
                .height(48.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.black)),
                onClick = {

                }) {
                Text(text = "Configuración de ubicación", color = Color.White,
                    style = typography.bodyMedium,
                    fontSize = 16.sp)
            }
        }


    }

}