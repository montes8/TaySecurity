package com.tay.taysecurity.android.ui.home.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.utils.manager.TaySureCall
import com.tay.taysecurity.android.utils.uiTayDialedNumber

@Composable
fun ContactScreen(
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mis contactos registrados",
            style = TextStyle(color = Color.Black, fontSize = 20.sp, fontFamily = FontFamily.Default)
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (TaySureCall.listContact.isNotEmpty()){
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(TaySureCall.listContact){contact ->
                    Column {
                        Divider(modifier = Modifier.height(8.dp),color = Color.White)
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(text = "A",modifier = Modifier.size(34.dp)
                                .padding(8.dp).align(Alignment.CenterVertically)
                                .drawBehind {
                                    drawCircle(
                                        color = Color.Black,
                                        radius = this.size.maxDimension
                                    )
                                }, color = Color.White,textAlign = TextAlign.Center
                               ,
                                fontSize = 14.sp)
                            Text(text = contact.name,modifier = Modifier
                                .padding(start = 24.dp)
                                .align(Alignment.CenterVertically),
                                style = typography.body2,
                                fontSize = 18.sp,color = Color.Black)
                            Spacer(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color.White))
                            IconButton(modifier = Modifier.padding(end = 4.dp), onClick = {
                                context.uiTayDialedNumber()
                            }) {
                                Icon(imageVector = Icons.Filled.Phone, contentDescription = "Menu Icon", tint = Color.Black,
                                )
                            }

                        }
                        Divider(modifier = Modifier.padding(top = 8.dp),color = Color.Black, thickness = 1.dp)
                    }

                }
            }
        }
    }
}