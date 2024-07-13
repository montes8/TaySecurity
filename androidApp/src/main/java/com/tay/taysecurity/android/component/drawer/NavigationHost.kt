package com.dev.leonardom.introuduccionajetpackcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tay.taysecurity.android.component.drawer.Destinations
import com.tay.taysecurity.android.ui.home.blocking.BlockingScreen
import com.tay.taysecurity.android.ui.home.extra.ExtraScreen
import com.tay.taysecurity.android.ui.home.security.SecurityScreen

@Composable
fun NavigationHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Destinations.BlockingScreen.route) {
        composable(Destinations.BlockingScreen.route) {
            BlockingScreen()
        }

        composable(
            Destinations.SecurityScreen.route
        ) {
            SecurityScreen()
        }

        composable(Destinations.ExtraScreen.route) {
            ExtraScreen()
        }
    }
}