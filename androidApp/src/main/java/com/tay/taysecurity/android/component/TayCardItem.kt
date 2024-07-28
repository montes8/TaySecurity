package com.tay.taysecurity.android.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
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
        backgroundColor = Color.Black,elevation = 4.dp) {
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
                    checkedThumbColor = Color.Magenta,
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor =  Color.Magenta,
                    uncheckedTrackColor = Color.White,
                )

            )
        }
    }

}