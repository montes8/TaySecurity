package com.tay.taysecurity.android.component.navigation

import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tay.taysecurity.android.ui.home.ScreenHome
import com.tay.taysecurity.android.ui.home.blocking.BlockingScreen
import com.tay.taysecurity.android.ui.home.extra.ExtraScreen
import com.tay.taysecurity.android.ui.home.image.ImageScreen
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
        composable(Destinations.SecurityScreen.route) { SecurityScreen(navControllerMain)}
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
        composable(route = DestinationsMain.InfoScreen.route +"/{dataInf}",
            arguments = listOf(
                navArgument("dataInfo") { type = NavType.StringType }
            )) {
            InfoScreen(navController, it.arguments?.getString("dataInfo")?:"")
        }
        composable(DestinationsMain.ImageScreen.route) { ImageScreen(navController)}
    }
}

sealed class Destinations(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object BlockingScreen: Destinations("contactos_screen", "Bloqueos", Icons.Filled.AppBlocking)
    data object SecurityScreen: Destinations("seguridad_screen", "Seguridad", Icons.Filled.Security)
    data object ExtraScreen: Destinations("extra_screen", "Ayuda", Icons.Filled.AddModerator)


}

sealed class DestinationsMain(
    val route: String
) {
    data object HomeScreen: DestinationsMain("home_screen")
    data object InfoScreen: DestinationsMain("info_screen")
    data object ImageScreen: DestinationsMain("image_screen")

    fun withArgs(vararg args: String = emptyArray(), optional: Map<String, String?> = emptyMap()): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }

            if(optional.isNotEmpty()) {
                append("?")
            }

            optional.entries.forEachIndexed { index, map ->
                map.value?.let {
                    if(index != 0) append("&")

                    append("${map.key}=${map.value}")
                }
            }
        }
    }
}