package com.tay.taysecurity.android.component

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tay.taysecurity.android.ui.blocking.ScreenBlockingCall
import com.tay.taysecurity.android.ui.home.ScreenHome

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenIdentifier.HomeScreen.route,
        route = ROOT_GRAPH_ROUTE) {

        composable(route = ScreenIdentifier.HomeScreen.route) {
            ScreenHome(navController = navController)
        }

        composable(route = ScreenIdentifier.BlockingCallScreen.route) {
            ScreenBlockingCall(navController)
        }

    }

}