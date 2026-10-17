package com.tay.taysecurity.android.component.navigation

import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tay.taysecurity.android.security.NavigationSecurityObserver
import com.tay.taysecurity.android.security.SecureDestination
import com.tay.taysecurity.android.ui.home.ScreenHome
import com.tay.taysecurity.android.ui.home.blocking.BlockingScreen
import com.tay.taysecurity.android.ui.home.extra.ExtraScreen
import com.tay.taysecurity.android.ui.image.ImageScreen
import com.tay.taysecurity.android.ui.info.InfoScreen
import com.tay.taysecurity.android.ui.home.security.SecurityScreen
import com.tay.taysecurity.android.ui.scan.ScanScreen
import com.tay.taysecurity.android.ui.wiifi.NetworkScreen
import kotlinx.serialization.Serializable

import com.tay.taysecurity.android.ui.stealth.StealthLockScreen

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    navControllerMain: NavHostController = rememberNavController(),
    navigationSecurityObserver: NavigationSecurityObserver? = null
) {
    LaunchedEffect(navController) {
        navigationSecurityObserver?.let { observer ->
            navController.addOnDestinationChangedListener { _, destination, _ ->
                observer.onDestinationChanged(destination)
            }
        }
    }

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
    navController: NavHostController = rememberNavController(),
    navigationSecurityObserver: NavigationSecurityObserver? = null,
    skipLockScreenInDebug: Boolean = com.tay.taysecurity.android.BuildConfig.DEBUG
) {
    LaunchedEffect(navController) {
        navigationSecurityObserver?.let { observer ->
            navController.addOnDestinationChangedListener { _, destination, _ ->
                observer.onDestinationChanged(destination)
            }
        }
    }

    val startDestination: DestinationsMain = if (skipLockScreenInDebug) {
        DestinationsMain.HomeScreen
    } else {
        DestinationsMain.StealthLockScreen
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        exitTransition = {
            ExitTransition.None
        }
    ) {

        composable<DestinationsMain.StealthLockScreen> {
            StealthLockScreen(
                onUnlockSuccess = {
                    navController.navigate(DestinationsMain.HomeScreen) {
                        popUpTo<DestinationsMain.StealthLockScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<DestinationsMain.HomeScreen> {
            ScreenHome(navController, navigationSecurityObserver)
        }


        composable<DestinationsMain.InfoScreen> { backStackEntry ->
            val infoArgs = backStackEntry.toRoute<DestinationsMain.InfoScreen>()
            InfoScreen(infoArgs.dataInfo)

        }


        composable<DestinationsMain.ImageScreen> {
            ImageScreen(navController)
        }

        composable<DestinationsMain.ScanScreen> {
            ScanScreen(
                onOpenRouterAdmin = { gatewayIp ->
                    navController.navigate(DestinationsMain.RouterAdminScreen(gatewayIp))
                }
            )
        }

        composable<DestinationsMain.RouterAdminScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<DestinationsMain.RouterAdminScreen>()
            com.tay.taysecurity.android.ui.scan.RouterAdminScreen(
                gatewayIp = args.gatewayIp,
                onBack = { navController.popBackStack() }
            )
        }

        composable<DestinationsMain.NetworkScreen> {
            NetworkScreen(
                onNetworkClick = { network ->
                    navController.navigate(
                        DestinationsMain.NetworkDetailScreen(
                            ssid = network.ssid,
                            bssid = network.bssid,
                            signalPercentage = network.signalPercentage,
                            rssidBm = network.rssidBm,
                            isSecured = network.isSecured,
                            securityType = network.securityType,
                            frequencyGhz = network.frequencyGhz,
                            isMobileHotspot = network.isMobileHotspot,
                            wifiStandard = network.wifiStandard,
                            distanceText = network.distanceText,
                            channel = network.channel,
                            channelWidth = network.channelWidth,
                            capabilities = network.capabilities,
                            hasDualBand = network.hasDualBand,
                            isPasspoint = network.isPasspoint,
                            venueName = network.venueName,
                            centerFreqs = network.centerFreqs,
                            beaconAgeMs = network.beaconAgeMs
                        )
                    )
                }
            )
        }

        composable<DestinationsMain.NetworkDetailScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<DestinationsMain.NetworkDetailScreen>()
            com.tay.taysecurity.android.ui.wiifi.NetworkDetailScreen(
                network = com.tay.taysecurity.android.ui.wiifi.model.AvailableWifiNetwork(
                    ssid = args.ssid,
                    bssid = args.bssid,
                    signalPercentage = args.signalPercentage,
                    rssidBm = args.rssidBm,
                    isSecured = args.isSecured,
                    securityType = args.securityType,
                    frequencyGhz = args.frequencyGhz,
                    isMobileHotspot = args.isMobileHotspot,
                    wifiStandard = args.wifiStandard,
                    distanceText = args.distanceText,
                    channel = args.channel,
                    channelWidth = args.channelWidth,
                    capabilities = args.capabilities,
                    hasDualBand = args.hasDualBand,
                    isPasspoint = args.isPasspoint,
                    venueName = args.venueName,
                    centerFreqs = args.centerFreqs,
                    beaconAgeMs = args.beaconAgeMs
                ),
                onBack = { navController.popBackStack() }
            )
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
    @Serializable object SecurityScreen : DestinationRoot, SecureDestination
    @Serializable object ExtraScreen : DestinationRoot
}

@Serializable
sealed interface DestinationsMain {
    @Serializable data object StealthLockScreen : DestinationsMain
    @Serializable data object HomeScreen : DestinationsMain
    @Serializable data class InfoScreen(val dataInfo : String) : DestinationsMain
    @Serializable data object ImageScreen : DestinationsMain
    @Serializable data object ScanScreen : DestinationsMain, SecureDestination
    @Serializable data class RouterAdminScreen(val gatewayIp: String) : DestinationsMain
    @Serializable data object NetworkScreen : DestinationsMain, SecureDestination
    @Serializable data class NetworkDetailScreen(
        val ssid: String,
        val bssid: String,
        val signalPercentage: Int,
        val rssidBm: Int,
        val isSecured: Boolean,
        val securityType: String,
        val frequencyGhz: String,
        val isMobileHotspot: Boolean,
        val wifiStandard: String,
        val distanceText: String,
        val channel: Int,
        val channelWidth: String,
        val capabilities: String,
        val hasDualBand: Boolean,
        val isPasspoint: Boolean,
        val venueName: String,
        val centerFreqs: String,
        val beaconAgeMs: String
    ) : DestinationsMain
}
