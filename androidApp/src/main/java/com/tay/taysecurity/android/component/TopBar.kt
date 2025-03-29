package com.tay.taysecurity.android.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    openDialog: () -> Unit
) {
    TopAppBar(
        contentColor = Color.Black,
        backgroundColor = Color.Black,
        title = { Text("Tay Security", modifier = Modifier.fillMaxWidth(), color = Color.White,textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu Icon", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = {
                openDialog()
            }) {
                Image(
                    painter = painterResource(com.tay.taysecurity.android.R.drawable.ic_gps),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
            }
        }
    )
}