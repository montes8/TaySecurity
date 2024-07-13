package com.tay.taysecurity.android.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tay.taysecurity.android.ui.home.ScreenHome

@Composable
fun Navigation(darkMode: MutableState<Boolean>) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenIdentifier.HomeScreen.route,
        route = ROOT_GRAPH_ROUTE
    ) {

        composable(route = ScreenIdentifier.HomeScreen.route) {
            ScreenHome()
        }

    }

}