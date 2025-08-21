package com.tay.taysecurity.android.ui.home.call

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.R
import com.tay.taysecurity.android.component.TopBarBack
import com.tay.taysecurity.android.utils.goCallNumber
import com.tay.taysecurity.android.utils.tayToast


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CallSureScreen(navController: NavHostController){
    val context = LocalContext.current

    var textNumber by remember { mutableStateOf("") }
    val numbers = arrayOf("1","2","3","4","5","6","7","8","9","*","0","#")
    Scaffold(
        topBar = {
            TopBarBack {
                navController.popBackStack()
            }
        }
    ){
        Column (modifier = Modifier.background(Color.White)
            .fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment =Alignment.CenterHorizontally){
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)){
                Text(
                    modifier = Modifier.background(Color.Transparent)
                        .weight(9f).align(Alignment.CenterVertically),
                    text = textNumber,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.gabi_regular)),
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold
                )
                if (textNumber.isNotEmpty()){
                    Image(
                        modifier = Modifier.weight(1f).clickable{
                            if (textNumber.isNotEmpty()){
                                val updateText = textNumber
                                textNumber = updateText.dropLast(1)
                            }
                        },
                        imageVector = Icons.AutoMirrored.Default.Backspace,
                        contentDescription = "FabIconCall",
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.CenterEnd,
                        colorFilter = ColorFilter.tint(color = Color.Magenta))
                }
                }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                color = Color.Black, thickness = 1.dp)
            Column (modifier = Modifier.background(Color.White).padding(32.dp),
                horizontalAlignment =Alignment.CenterHorizontally){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3)
                ) {
                    items(numbers) {number->
                        Card(modifier = Modifier.padding(8.dp).clickable{
                            val textCurrent = textNumber
                            textNumber = textCurrent+number

                        },
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = Color.Black,elevation = 4.dp) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(modifier = Modifier.padding(16.dp),
                                    text = number,
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.gabi_regular)),
                                    fontWeight= FontWeight.Bold,
                                    color = Color.Magenta
                                )
                            }
                        }

                    }
                }
            }


            FloatingActionButton(onClick = {
                if (textNumber.isEmpty()){
                    context.tayToast("Ingrese un numero")
                }else{
                    textNumber.goCallNumber(context)
                }
            },modifier = Modifier.border( width = 2.dp,
                    color = Color.Magenta,
                    shape = RoundedCornerShape(30.dp)).size(70.dp)

                , backgroundColor = Color.Black, contentColor = Color.Black) {
                Image(imageVector = Icons.Default.Call, contentDescription = "FabIconCall",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(color = Color.Magenta))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
