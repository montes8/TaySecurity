package com.tay.taysecurity.android.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val checked = remember { mutableStateOf(false) }
    checked.value = state
    Card(shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black, //Card background color
            contentColor = Color.Black  //Card content color,e.g.text
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row{
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
            Switch(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = checked.value,
                onCheckedChange = {
                    checked.value = it
                    tayClickItem.invoke(it)
                },colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Green,
                    checkedTrackColor = Color.LightGray,
                    checkedBorderColor = Color.Green,
                    uncheckedThumbColor = Color.Red,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = Color.Red,
                    disabledCheckedThumbColor = Color.Green.copy(alpha = ContentAlpha.disabled),
                    disabledCheckedTrackColor = Color.LightGray.copy(alpha = ContentAlpha.disabled),
                    disabledCheckedBorderColor = Color.Green.copy(alpha = ContentAlpha.disabled),
                    disabledUncheckedThumbColor = Color.Red.copy(alpha = ContentAlpha.disabled),
                    disabledUncheckedTrackColor = Color.LightGray.copy(alpha = ContentAlpha.disabled),
                    disabledUncheckedBorderColor = Color.Red.copy(alpha = ContentAlpha.disabled),
                )
            )
        }
    }

}