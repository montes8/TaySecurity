package com.tay.taysecurity.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.material.DrawerValue
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tay.taysecurity.android.component.FloatingActionButtonsSure
import com.tay.taysecurity.android.component.TopBar
import com.tay.taysecurity.android.component.drawer.Drawer
import com.tay.taysecurity.android.component.navigation.BottomNavigationBar
import com.tay.taysecurity.android.component.navigation.Destinations
import com.tay.taysecurity.android.component.navigation.NavigationHost
import com.tay.taysecurity.android.utils.uiTayViewCallButton


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenHome(navControllerMain: NavHostController){
    val context = LocalContext.current
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()
    val navigationItems = listOf(
        Destinations.BlockingScreen,
        Destinations.SecurityScreen,
        Destinations.ExtraScreen
    )

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController = navController, items = navigationItems)},
        floatingActionButton = {
            FloatingActionButtonsSure{
                if (it){
                    context.uiTayViewCallButton()
                }
            }
         },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            TopBar(
                scope,
                scaffoldState,
            )
        },
        drawerContent = { Drawer(scope, scaffoldState, navController, items = navigationItems) },
        drawerGesturesEnabled = true
    ){
        NavigationHost(navController,navControllerMain)
    }
}