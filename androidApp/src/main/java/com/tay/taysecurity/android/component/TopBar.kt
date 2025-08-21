package com.tay.taysecurity.android.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    TopAppBar(
        contentColor = Color.Black,
        backgroundColor = Color.Black,
        title = { Text("Tay Security",
            modifier = Modifier.fillMaxWidth(), color = Color.White,textAlign = TextAlign.Center) },
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
                 //not implement
             }) {
                 Image(
                     painter = painterResource(com.tay.taysecurity.android.R.drawable.ic_gps),
                     contentScale = ContentScale.Crop,
                     contentDescription = "", colorFilter = ColorFilter.tint(Color.Black)
                 )
             }
        }
    )
}

@Composable
fun TopBarBack(
    openClick: () -> Unit
) {
    TopAppBar(
        contentColor = Color.Black,
        backgroundColor = Color.Black,
        title = { Text("Tay Security", modifier = Modifier.fillMaxWidth(), color = Color.White,textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = {
                openClick.invoke()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Menu Icon", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = {
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu Icon", tint = Color.Black)

            }
        }
    )
}