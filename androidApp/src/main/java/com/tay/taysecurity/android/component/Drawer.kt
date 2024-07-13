package com.tay.taysecurity.android.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.component.navigation.Destinations
import com.tay.taysecurity.android.component.navigation.currentRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.tay.taysecurity.android.R

@Composable
fun Drawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    items: List<Destinations>
) {
    Column {
        Image(
            painter = painterResource(id = R.drawable.ic_bg_header),
            contentDescription = "Bg Image",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.dp))

        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            DrawerItem(item = item, selected = currentRoute == item.route) {
                navController.navigate(item.route) {
                    launchSingleTop = true
                }

                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    item: Destinations,
    selected: Boolean,
    onItemClick: (Destinations) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(6.dp)
            .clip(RoundedCornerShape(12))
            .background(if (selected) Color.Black else Color.Transparent)
            .padding(8.dp)
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if(selected) Color.White else Color.Gray
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text= item.title,
            style = TextStyle(fontSize = 18.sp),
            color = if(selected) Color.White else Color.Black
        )
    }
}








