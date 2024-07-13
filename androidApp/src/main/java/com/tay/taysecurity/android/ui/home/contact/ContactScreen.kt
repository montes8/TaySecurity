package com.tay.taysecurity.android.ui.home.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.model.temporary.DataTemporary

@Composable
fun ContactScreen(
) {
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
        if (DataTemporary.listContact.isNotEmpty()){
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(DataTemporary.listContact){contact ->
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(text = "A",modifier = Modifier
                            .padding(8.dp)
                            .drawBehind {
                                drawCircle(
                                    color = Color.Black,
                                    radius = this.size.maxDimension
                                )
                            }, color = Color.White)
                        Text(text = contact.name,modifier = Modifier
                            .padding(start = 24.dp)
                            .align(Alignment.CenterVertically),
                            style = TextStyle(color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily.Default))
                        Icon(imageVector = Icons.Filled.Phone, contentDescription = "Menu Icon", tint = Color.Black)
                    }
                }
            }
        }
    }
}