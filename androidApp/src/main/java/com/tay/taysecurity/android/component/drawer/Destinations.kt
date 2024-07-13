package com.tay.taysecurity.android.component.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object BlockingScreen: Destinations("contactos_screen", "Bloqueos", Icons.Filled.AppBlocking)
    data object SecurityScreen: Destinations("seguridad_screen", "Seguridad", Icons.Filled.Security)
    data object ExtraScreen: Destinations("extra_screen", "Extra", Icons.Filled.AddModerator)
}
