package com.tay.taysecurity.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.material.DrawerValue
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.dev.leonardom.introuduccionajetpackcompose.navigation.NavigationHost
import com.tay.taysecurity.android.component.BottomNavigationBar
import com.tay.taysecurity.android.component.Dialog
import com.tay.taysecurity.android.component.TopBar
import com.tay.taysecurity.android.component.drawer.Destinations
import com.tay.taysecurity.android.component.drawer.Drawer
import com.tay.taysecurity.android.utils.uiTayViewCallButton


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenHome(){
    val context = LocalContext.current

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }

    val navigationItems = listOf(
        Destinations.BlockingScreen,
        Destinations.SecurityScreen,
        Destinations.ExtraScreen
    )

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController = navController, items = navigationItems) },
        floatingActionButton = { FloatingActionButton(onClick = {
            context.uiTayViewCallButton()
        }, backgroundColor = Color.Black, contentColor = Color.Black) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Fab Icon", tint = Color.White)
        } },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            TopBar(
                scope,
                scaffoldState,
                openDialog = { openDialog.value = true},
            )
        },
        drawerContent = { Drawer(scope, scaffoldState, navController, items = navigationItems) },
        drawerGesturesEnabled = true
    ){
        NavigationHost(navController)
    }

    Dialog(showDialog = openDialog.value, dismissDialog = { openDialog.value = false })

}