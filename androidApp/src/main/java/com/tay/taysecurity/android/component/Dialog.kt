package com.tay.taysecurity.android.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.R


@Composable
fun DialogSure(
    showDialog: Boolean,
    typeDeveloper : Boolean = false,
    dismissDialog: (Boolean) -> Unit
) {
    if(showDialog){
        AlertDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp),
                    ),
            backgroundColor = Color.White,
            onDismissRequest = { },
            title = { Text(if(typeDeveloper)"Activa modo desarrollador" else "Estable como predeterminada",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.gabi_regular)),
                fontWeight= FontWeight.Bold
            ) },
            text = {
                Column {
                    Text(if(typeDeveloper)"Primero debes activar el modo desarrollador y establecer como app de ubicación,sigue los pasos de la seccion de ayuda." else
                        "Estable la App de llamada o SMS como predeterminada para poder usar las funcionalidades de bloqueo.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.gabi_regular)),
                        fontWeight= FontWeight.Normal)
                }
            },
            confirmButton = {
                Button(shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                    ,onClick = { dismissDialog(true) }) {
                    Text(if(typeDeveloper)"Aceptar" else "Redirigir",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.gabi_regular)),
                        fontWeight= FontWeight.Bold)
                }
            },
            dismissButton = {
                Button( shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color.Black),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    onClick = { dismissDialog(false) }) {
                    Text("Cancelar",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.gabi_regular)),
                        fontWeight= FontWeight.Bold)
                }
            }
        )
    }
}