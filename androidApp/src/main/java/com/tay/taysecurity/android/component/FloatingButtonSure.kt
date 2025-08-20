package com.tay.taysecurity.android.component
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp


@Composable
fun FloatingActionButtonsSure(clickBottom: (Boolean) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            FloatingActionButton(onClick = { clickBottom.invoke(true) },
                modifier = Modifier.border( width = 2.dp,
                    color = Color.Magenta,
                    shape = RoundedCornerShape(20.dp)).size(40.dp).align(Alignment.Bottom)

                , backgroundColor = Color.Black, contentColor = Color.Black) {
                Image(imageVector = Icons.Default.Apps, contentDescription = "FabIconCall",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(color = Color.Magenta))
            }
            FloatingActionButton(onClick = { clickBottom.invoke(false) },
                modifier = Modifier.border( width = 2.dp,
                    color = Color.Magenta,
                    shape = RoundedCornerShape(30.dp))

                , backgroundColor = Color.Black, contentColor = Color.Black) {
                Image(imageVector = Icons.Default.Call, contentDescription = "FabIconCall",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(color = Color.Magenta))
            }
        }
    }
}