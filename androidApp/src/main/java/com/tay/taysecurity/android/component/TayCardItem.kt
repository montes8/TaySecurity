package com.tay.taysecurity.android.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tay.taysecurity.android.R


@Composable
fun TayCardItem(
     state : Boolean,
    text: String,
    subText: String,
    tayClickItem: (Boolean) -> Unit
) {
    Log.d("TAGTay","$state")
    val checked = remember { mutableStateOf(false) }
    checked.value = state
    Card(shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.Black,elevation = 4.dp) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Column(modifier = Modifier
                .weight(4.0f)
                .padding(16.dp)) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_semi_bold)),
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding( top = 2.dp),
                    text = subText,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ui_tay_montserrat_medium)),
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .height(30.dp)
                    .width(70.dp)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .border(1.dp, Color.Magenta, CircleShape)
                    .background(Color.White).uiTayNoRippleClickable{
                        tayClickItem.invoke(!state)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement= if(checked.value)Arrangement.End else Arrangement.Start

            ) {
                Image(
                    painter = painterResource(R.drawable.tay_circle_white),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp).padding(2.dp)
                        .clip(CircleShape)                       // clip to the circle shape
                        .border(2.dp, Color.Black, CircleShape)
                        .background(Color.Magenta) ,
                    contentDescription = "",
                    alignment = Alignment.Center
                )
            }
        }
    }

}


@Composable
fun Modifier.uiTayNoRippleClickable(
    onClick: () -> Unit
) = this.then(
      Modifier.clickable(
          indication = null,
          interactionSource = remember { MutableInteractionSource() }) {
          onClick()
      }
    )
