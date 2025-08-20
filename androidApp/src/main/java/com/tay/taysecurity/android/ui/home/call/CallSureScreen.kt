package com.tay.taysecurity.android.ui.home.call

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tay.taysecurity.android.component.TopBarBack


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CallSureScreen(navController: NavHostController){

    val numbers = arrayOf("1","2","3","4","5","6","7","8","9","*","0","#")
    Scaffold(
        topBar = {
            TopBarBack {
                navController.popBackStack()
            }
        }
    ){
        Column (modifier = Modifier.background(Color.White)
            .fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment =Alignment.CenterHorizontally){
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                items(numbers) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = it)
                    }
                }
            }
        }
    }
}
