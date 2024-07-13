package com.tay.taysecurity.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.material.DrawerValue
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.dev.leonardom.introuduccionajetpackcompose.navigation.NavigationHost
import com.tay.taysecurity.android.component.BottomNavigationBar
import com.tay.taysecurity.android.component.Dialog
import com.tay.taysecurity.android.component.drawer.Drawer
import com.tay.taysecurity.android.component.TopBar
import com.tay.taysecurity.android.component.drawer.Destinations

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenHome(){
    val context = LocalContext.current


  /*  Column(modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier
            .fillMaxWidth()
            .height(56.dp).padding(top = 24.dp)
            .background(colorResource(R.color.black))){
            Text(text = stringResource(R.string.app_title), modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically), color = colorResource(R.color.white)
                , textAlign = TextAlign.Center,  style = typography.bodyMedium,
                fontSize = 20.sp, )
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp),verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(modifier = Modifier.fillMaxWidth()
                .height(48.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.black)),
                onClick = {
                    context.uiTayViewDialedNumber()
                     val i = Intent()
                     i.setClass(context, Call::class.java)
                     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(i)
                    //TaySureCall.taySureFinalizeCall()
                  //  navController.navigate(ScreenIdentifier.BlockingCallScreen.route)
                }) {
                Text(text = "Configuración de llamada", color = Color.White,
                    style = typography.bodyMedium,
                    fontSize = 16.sp)
            }
            Divider(color = Color.Black, thickness = 24.dp)
            Button(modifier = Modifier.fillMaxWidth()
                .height(48.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.black)),
                onClick = {

                }) {
                Text(text = "Configuración de ubicación", color = Color.White,
                    style = typography.bodyMedium,
                    fontSize = 16.sp)
            }
        }

    }*/



    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }

    val navigationItems = listOf(
        Destinations.ContactScreen,
        Destinations.SecurityScreen,
        Destinations.ExtraScreen
    )

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController = navController, items = navigationItems) },
        floatingActionButton = { FloatingActionButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Fab Icon")
        } },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            TopBar(
                scope,
                scaffoldState,
                openDialog = { openDialog.value = true},
            )
        },
        drawerContent = { Drawer(scope, scaffoldState, navController, items = navigationItems) },
        drawerGesturesEnabled = true
    ){
        NavigationHost(navController)
    }

    Dialog(showDialog = openDialog.value, dismissDialog = { openDialog.value = false })

}