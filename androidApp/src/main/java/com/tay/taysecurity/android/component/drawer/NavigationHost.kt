package com.tay.taysecurity.android.component.drawer

import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tay.taysecurity.android.ui.home.ScreenHome
import com.tay.taysecurity.android.ui.home.blocking.BlockingScreen
import com.tay.taysecurity.android.ui.home.extra.ExtraScreen
import com.tay.taysecurity.android.ui.home.info.InfoScreen
import com.tay.taysecurity.android.ui.home.security.SecurityScreen

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    navControllerMain: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Destinations.BlockingScreen.route,
       exitTransition = {
        ExitTransition.None
    }) {
        composable(Destinations.BlockingScreen.route) { BlockingScreen()}
        composable(Destinations.SecurityScreen.route) { SecurityScreen()}
        composable(Destinations.ExtraScreen.route) {ExtraScreen(navControllerMain) }
    }
}


@Composable
fun NavigationHostMain(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = DestinationsMain.HomeScreen.route,
        exitTransition = {
            ExitTransition.None
        }) {
        composable(DestinationsMain.HomeScreen.route) { ScreenHome(navController)}
        composable(DestinationsMain.InfoScreen.route) {InfoScreen(navController) }
    }
}