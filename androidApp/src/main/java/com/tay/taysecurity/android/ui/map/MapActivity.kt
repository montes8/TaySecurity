package com.tay.taysecurity.android.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.tay.taysecurity.android.utils.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : ComponentActivity() {
    companion object{
        fun newInstance(context: Context) = context.startActivity(Intent(context,MapActivity::class.java))
    }

    private val viewModel: MapViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MapScreen(viewModel)
            }
        }

    }
}

