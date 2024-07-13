package com.tay.taysecurity.android.component

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    openDialog: () -> Unit,
    displaySnackBar: () -> Unit
) {
    TopAppBar(
        title = { Text("Hola") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu Icon")
            }
        },
        actions = {
            IconButton(onClick = {
                displaySnackBar()
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }

            IconButton(onClick = {
                openDialog()
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }
        }
    )
}