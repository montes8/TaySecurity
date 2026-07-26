package com.tay.taysecurity.android.component.navigation

import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tay.taysecurity.android.ui.home.ScreenHome
import com.tay.taysecurity.android.ui.home.blocking.BlockingScreen
import com.tay.taysecurity.android.ui.home.extra.ExtraScreen
import com.tay.taysecurity.android.ui.home.image.ImageScreen
import com.tay.taysecurity.android.ui.home.info.InfoScreen
import com.tay.taysecurity.android.ui.home.security.SecurityScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    navControllerMain: NavHostController = rememberNavController()
) {
    NavHost(navController = navController,
        startDestination = DestinationRoot.BlockingScreen,
       exitTransition = {
        ExitTransition.None
    }) {

        composable<DestinationRoot.BlockingScreen> {
            BlockingScreen()
        }

        composable<DestinationRoot.SecurityScreen> {
            SecurityScreen(navControllerMain)
        }

        composable<DestinationRoot.ExtraScreen> {
            ExtraScreen(navControllerMain)
        }

    }
}


@Composable
fun NavigationHostMain(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = DestinationsMain.HomeScreen,
        exitTransition = {
            ExitTransition.None
        }) {
        composable<DestinationsMain.HomeScreen> {
            ScreenHome(navController)
        }


        composable<DestinationsMain.InfoScreen> { backStackEntry ->
            val infoArgs = backStackEntry.toRoute<DestinationsMain.InfoScreen>()
            InfoScreen(navController,infoArgs.dataInfo)

        }


        composable<DestinationsMain.ImageScreen> {
            ImageScreen(navController)
        }

    }
}

sealed class Destinations(
    val route: DestinationRoot,
    val title: String,
    val icon: ImageVector
) {
    data object BlockingScreen: Destinations(DestinationRoot.BlockingScreen,"Bloqueos",Icons.Filled.AppBlocking)
    data object SecurityScreen: Destinations(DestinationRoot.SecurityScreen,"Seguridad",Icons.Filled.Security)
    data object ExtraScreen: Destinations(DestinationRoot.ExtraScreen,"Ayuda",Icons.Filled.AddModerator)

}

@Serializable
sealed interface DestinationRoot {
    @Serializable object BlockingScreen : DestinationRoot
    @Serializable object SecurityScreen : DestinationRoot
    @Serializable object ExtraScreen : DestinationRoot
}

@Serializable
sealed interface DestinationsMain {
    @Serializable data object HomeScreen : DestinationsMain
    @Serializable data class InfoScreen(val dataInfo : String) : DestinationsMain
    @Serializable data object ImageScreen : DestinationsMain

}
