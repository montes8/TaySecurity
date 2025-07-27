package com.tay.taysecurity.android.ui.home.extra

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TayCardItemNext
import com.tay.taysecurity.android.component.drawer.DestinationsMain

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ExtraScreen( navController: NavHostController) {
    Column( modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(start = 8.dp, end = 8.dp, bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
            AndroidLogo(
                backgroundColor = Color.White,
                contentColor =  Color.Black,
                padding = 30.dp
            )
        }

        Text(
            text = stringResource(R.string.title_helps_view),
            color = Color.Black,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.gabi_regular)),
            fontWeight= FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "BLOQUEOS DE LLAMADA",
            subText ="Aqui te explicamos como se usa esta funcionalidad a detalle, y las configuraciones a usar."){
            navController.navigate(DestinationsMain.InfoScreen.route)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "BLOQUEOS DE MESAJES DE TEXTO",
            subText ="Aqui te explicamos como se usa esta funcionalidad a detalle, y las configuraciones a usar."){
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "UBICACION ALEATORIA",
            subText ="Aqui te explicamos como se usa esta funcionalidad a detalle, y las configuraciones a usar."){
        }

        Spacer(modifier = Modifier.height(16.dp))
        TayCardItemNext(
            text = "INFORMACION DE IMAGEN",
            subText ="Aqui te explicamos como se usa esta funcionalidad a detalle, y las configuraciones a usar."){
        }

    }

}

@Composable
fun AndroidLogo(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color,
    padding: Dp = 0.dp
) {
    val eyesOffset = remember { mutableStateOf(0.0f) }

    Canvas(
        modifier = modifier
            .width(150.dp).height(150.dp)
            .padding(padding)
            .background(backgroundColor)
            .pointerInput(Unit){
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()

                        eyesOffset.value += dragAmount.x * 0.50f
                    },
                    onDragEnd = {
                        eyesOffset.value = 0f
                    }
                )
            }
    ){
        drawArc(
            startAngle = -180f,
            sweepAngle = 180f,
            useCenter = false,
            color = contentColor,
            size = Size(size.minDimension, size.minDimension),
            topLeft = Offset(0f, size.minDimension * 0.5f)
        )

        drawCircle(
            color = Color.Magenta,
            center = Offset(size.minDimension * 0.3f + eyesOffset.value, size.minDimension * 0.8f),
            radius = size.minDimension * 0.04f
        )

        drawCircle(
            color = Color.Magenta,
            center = Offset(size.minDimension * 0.7f + eyesOffset.value, size.minDimension * 0.8f),
            radius = size.minDimension * 0.04f
        )

        rotate(
            degrees = 340f,
            pivot = Offset(size.minDimension * 0.2f, size.minDimension * 0.4f)
        ){
            drawRoundRect(
                color = contentColor,
                size = Size(size.minDimension * 0.035f, size.minDimension * 0.22f),
                cornerRadius = CornerRadius(size.minDimension * 0.02f),
                topLeft = Offset(size.minDimension * 0.2f + (eyesOffset.value * 0.3f), size.minDimension * 0.4f)
            )
        }

        rotate(
            degrees = 20f,
            pivot = Offset(size.minDimension * 0.8f, size.minDimension * 0.4f)
        ){
            drawRoundRect(
                color = contentColor,
                size = Size(size.minDimension * 0.035f, size.minDimension * 0.22f),
                cornerRadius = CornerRadius(size.minDimension * 0.02f),
                topLeft = Offset(size.minDimension * 0.8f + (eyesOffset.value * 0.3f), size.minDimension * 0.4f)
            )
        }
    }
}







