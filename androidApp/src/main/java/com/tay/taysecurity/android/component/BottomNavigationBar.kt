package com.tay.taysecurity.android.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.component.drawer.Destinations
import com.tay.taysecurity.android.component.drawer.currentRoute

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<Destinations>
) {
    val currentRoute = currentRoute(navController)
    BottomNavigation(
        backgroundColor = Color.Black,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
        contentColor = Color.Black,

    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            BottomNavigationItem(
                selectedContentColor = Color.Yellow,
                unselectedContentColor = Color.White,
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title,
                    tint = if(selected)Color.Yellow else Color.White) },
                label = { Text(screen.title, color = if(selected)Color.Yellow else Color.White) },
                selected = selected,
                onClick = {
                      navController.navigate(screen.route) {
                          popUpTo(navController.graph.findStartDestination().id){
                              saveState = true
                          }
                          launchSingleTop = true
                      }
                },
                alwaysShowLabel = true
            )
        }
    }
}