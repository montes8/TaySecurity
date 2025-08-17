package com.tay.taysecurity.android.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
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
    title : String = "Estable como predeterminada",
    message : String = "Estable la App de llamada o SMS como predeterminada para poder usar las funcionalidades de bloqueo.",
    dismissDialog: (Boolean) -> Unit
) {
    if(showDialog){
        AlertDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)).background(Color.White),
            onDismissRequest = { },
            title = { Text(title, style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            ) },
            text = {
                Column {
                    Text(message)
                }
            },
            confirmButton = {
                Button(shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta)
                    ,onClick = { dismissDialog(true) }) {
                    Text("Redirigir",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Magenta,
                        fontFamily = FontFamily(Font(R.font.gabi_regular)),
                        fontWeight= FontWeight.Bold)
                }
            },
            dismissButton = {
                Button( shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color.Magenta),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    onClick = { dismissDialog(false) }) {
                    Text("Cancelar",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.gabi_regular)),
                        fontWeight= FontWeight.Normal)
                }
            }
        )
    }
}